package com.sejong.elasticservice.internal.service;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptScoreQuery;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class PopularContentServicePerformanceTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final int DATA_SIZE = 1000; // 테스트 데이터 개수
    private static final int ITERATIONS = 10;  // 반복 횟수

    @BeforeEach
    void setUp() throws InterruptedException {
        // 기존 데이터 삭제 및 인덱스 재생성
        try {
            elasticsearchOperations.indexOps(ProjectDocument.class).delete();
            elasticsearchOperations.indexOps(ProjectDocument.class).create();
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Setup error (ignored): " + e.getMessage());
        }

        // 대량 테스트 데이터 생성
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= DATA_SIZE; i++) {
            ProjectDocument project = ProjectDocument.builder()
                    .id(String.valueOf(i))
                    .title("프로젝트 " + i)
                    .description("설명 " + i)
                    .projectStatus(ProjectStatus.IN_PROGRESS)
                    .createdAt(now.minusDays(i % 7).format(FORMATTER))
                    .updatedAt(now.minusDays(i % 7).format(FORMATTER))
                    .likeCount((long) (Math.random() * 100))
                    .viewCount((long) (Math.random() * 1000))
                    .build();
            elasticsearchOperations.save(project);
        }

        // 인덱싱 대기
        Thread.sleep(2000);
        System.out.println("테스트 데이터 " + DATA_SIZE + "개 생성 완료");
    }

    @Test
    void compareScriptScoreVsScriptSort() {
        String oneWeekAgo = LocalDateTime.now().minusWeeks(1).format(FORMATTER);

        // 1. Script Score Query 성능 측정
        long scriptScoreTotal = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            searchWithScriptScore(oneWeekAgo);
            long duration = System.nanoTime() - start;
            scriptScoreTotal += duration;
            System.out.println("Script Score Query #" + (i + 1) + ": " + duration / 1_000_000.0 + " ms");
        }

        // 2. Script Sort 성능 측정
        long scriptSortTotal = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            searchWithScriptSort(oneWeekAgo);
            long duration = System.nanoTime() - start;
            scriptSortTotal += duration;
            System.out.println("Script Sort #" + (i + 1) + ": " + duration / 1_000_000.0 + " ms");
        }

        // 3. 결과 출력
        double scriptScoreAvg = scriptScoreTotal / (double) ITERATIONS / 1_000_000.0;
        double scriptSortAvg = scriptSortTotal / (double) ITERATIONS / 1_000_000.0;

        System.out.println("\n========== 성능 비교 결과 ==========");
        System.out.println("데이터 개수: " + DATA_SIZE);
        System.out.println("반복 횟수: " + ITERATIONS);
        System.out.println("----------------------------------------");
        System.out.println("Script Score Query 평균: " + String.format("%.2f", scriptScoreAvg) + " ms");
        System.out.println("Script Sort 평균: " + String.format("%.2f", scriptSortAvg) + " ms");
        System.out.println("----------------------------------------");

        if (scriptScoreAvg < scriptSortAvg) {
            double improvement = ((scriptSortAvg - scriptScoreAvg) / scriptScoreAvg) * 100;
            System.out.println("✅ Script Score Query가 " + String.format("%.1f", improvement) + "% 더 빠름");
        } else {
            double improvement = ((scriptScoreAvg - scriptSortAvg) / scriptSortAvg) * 100;
            System.out.println("✅ Script Sort가 " + String.format("%.1f", improvement) + "% 더 빠름");
        }
        System.out.println("====================================\n");
    }

    private List<ProjectDocument> searchWithScriptScore(String oneWeekAgo) {
        Query rangeQuery = Query.of(q -> q
                .range(r -> r
                        .date(d -> d
                                .field("createdAt")
                                .gte(oneWeekAgo)
                        )
                )
        );

        // Script Score Query
        Query scriptScoreQuery = Query.of(q -> q
                .scriptScore(s -> s
                        .query(rangeQuery)
                        .script(Script.of(sc -> sc
                                .source("doc['likeCount'].value * 2 + doc['viewCount'].value")
                        ))
                )
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(scriptScoreQuery)
                .withSort(Sort.by(Sort.Order.desc("_score")))
                .withPageable(PageRequest.of(0, 1))
                .build();

        SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(searchQuery, ProjectDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }

    private List<ProjectDocument> searchWithScriptSort(String oneWeekAgo) {
        Query rangeQuery = Query.of(q -> q
                .range(r -> r
                        .date(d -> d
                                .field("createdAt")
                                .gte(oneWeekAgo)
                        )
                )
        );

        // Script Sort
        SortOptions scriptSort = SortOptions.of(s -> s
                .script(sc -> sc
                        .script(script -> script
                                .source("doc['likeCount'].value * 2 + doc['viewCount'].value")
                        )
                        .order(SortOrder.Desc)
                        .type(co.elastic.clients.elasticsearch._types.ScriptSortType.Number)
                )
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(rangeQuery)
                .withSort(List.of(scriptSort))
                .withPageable(PageRequest.of(0, 1))
                .build();

        SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(searchQuery, ProjectDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
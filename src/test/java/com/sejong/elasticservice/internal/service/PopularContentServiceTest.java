package com.sejong.elasticservice.internal.service;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.internal.dto.ContentResponse;
import com.sejong.elasticservice.news.domain.Content;
import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class PopularContentServiceTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private PopularContentService popularContentService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @BeforeEach
    void setUp() {
        // 테스트 전 기존 데이터 삭제
        try {
            // 모든 인덱스의 모든 문서 삭제
            elasticsearchOperations.indexOps(ProjectDocument.class).delete();
            elasticsearchOperations.indexOps(NewsDocument.class).delete();
            elasticsearchOperations.indexOps(CsKnowledgeDocument.class).delete();

            // 인덱스 재생성 (매핑은 Document 클래스에서 자동 생성)
            elasticsearchOperations.indexOps(ProjectDocument.class).create();
            elasticsearchOperations.indexOps(NewsDocument.class).create();
            elasticsearchOperations.indexOps(CsKnowledgeDocument.class).create();

            Thread.sleep(1000); // 인덱스 생성 대기
        } catch (Exception e) {
            System.out.println("Setup error (ignored): " + e.getMessage());
        }
    }

    @Test
    void testPopularContentWithSampleData() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // Project 샘플 데이터 (5개)
        List<ProjectDocument> projects = List.of(
                createProject("1", "프로젝트 A", now.minusDays(1), 10L, 100L),
                createProject("2", "프로젝트 B", now.minusDays(2), 50L, 200L),  // 인기도: 300
                createProject("3", "프로젝트 C", now.minusDays(3), 30L, 150L),
                createProject("4", "프로젝트 D", now.minusDays(4), 5L, 50L),
                createProject("5", "프로젝트 E", now.minusDays(5), 100L, 500L)   // 인기도: 700 (최고)
        );

        // News 샘플 데이터 (5개)
        List<NewsDocument> newsList = List.of(
                createNews("1", "뉴스 A", now.minusDays(1), 20L, 80L),
                createNews("2", "뉴스 B", now.minusDays(2), 60L, 300L),    // 인기도: 420
                createNews("3", "뉴스 C", now.minusDays(3), 40L, 200L),
                createNews("4", "뉴스 D", now.minusDays(4), 10L, 100L),
                createNews("5", "뉴스 E", now.minusDays(5), 80L, 400L)     // 인기도: 560
        );

        // CsKnowledge 샘플 데이터 (5개)
        List<CsKnowledgeDocument> csKnowledges = List.of(
                createCsKnowledge("1", "CS 지식 A", now.minusDays(1), 15L, 90L),
                createCsKnowledge("2", "CS 지식 B", now.minusDays(2), 70L, 350L),  // 인기도: 490
                createCsKnowledge("3", "CS 지식 C", now.minusDays(3), 45L, 250L),
                createCsKnowledge("4", "CS 지식 D", now.minusDays(4), 12L, 110L),
                createCsKnowledge("5", "CS 지식 E", now.minusDays(5), 90L, 450L)   // 인기도: 630
        );

        // 데이터 저장
        projects.forEach(elasticsearchOperations::save);
        newsList.forEach(elasticsearchOperations::save);
        csKnowledges.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // 가장 인기 있는 컨텐츠 조회
        ContentResponse result = popularContentService.getMostPopularContent();

        // 검증: 프로젝트 E가 선택되어야 함 (인기도 700)
        Assertions.assertNotNull(result);
        Assertions.assertEquals("프로젝트 E", result.getTitle());
        Assertions.assertEquals("PROJECT", result.getContentType());
        Assertions.assertEquals(100L, result.getLikeCount());
        Assertions.assertEquals(500L, result.getViewCount());
        Assertions.assertEquals(700.0, result.calculatePopularityScore());
    }

    @Test
    void testWithOver100Documents() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // 150개의 프로젝트 생성 (100개 제한 테스트)
        for (int i = 1; i <= 150; i++) {
            ProjectDocument project = createProject(
                    String.valueOf(i),
                    "프로젝트 " + i,
                    now.minusDays(i % 7), // 최근 1주일 내
                    (long) (i * 2), // likeCount
                    (long) (i * 10)  // viewCount
            );
            elasticsearchOperations.save(project);
        }

        // 최고 인기도를 가진 문서를 마지막에 추가 (151번째)
        ProjectDocument topProject = createProject(
                "999",
                "슈퍼 인기 프로젝트",
                now.minusDays(1),
                1000L,  // likeCount * 2 = 2000
                5000L   // viewCount = 5000
                // 인기도: 7000
        );
        elasticsearchOperations.save(topProject);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // 가장 인기 있는 컨텐츠 조회
        ContentResponse result = popularContentService.getMostPopularContent();

        // 검증: 슈퍼 인기 프로젝트가 선택되어야 함 (인기도 7000)
        Assertions.assertEquals(7000, result.calculatePopularityScore());
    }

    private ProjectDocument createProject(String id, String title, LocalDateTime createdAt, Long likeCount, Long viewCount) {
        return ProjectDocument.builder()
                .id(id)
                .title(title)
                .description("프로젝트 설명: " + title)
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .createdAt(createdAt.format(FORMATTER))
                .updatedAt(createdAt.format(FORMATTER))
                .likeCount(likeCount)
                .viewCount(viewCount)
                .build();
    }

    private NewsDocument createNews(String id, String title, LocalDateTime createdAt, Long likeCount, Long viewCount) {
        Content content = Content.builder()
                .title(title)
                .content("뉴스 내용: " + title)
                .build();

        return NewsDocument.builder()
                .id(id)
                .content(content)
                .writerId("writer-" + id)
                .createdAt(createdAt.format(FORMATTER))
                .updatedAt(createdAt.format(FORMATTER))
                .likeCount(likeCount)
                .viewCount(viewCount)
                .build();
    }

    private CsKnowledgeDocument createCsKnowledge(String id, String title, LocalDateTime createdAt, Long likeCount, Long viewCount) {
        return CsKnowledgeDocument.builder()
                .id(id)
                .title(title)
                .content("CS 지식 내용: " + title)
                .category("알고리즘")
                .createdAt(createdAt.format(FORMATTER))
                .likeCount(likeCount)
                .viewCount(viewCount)
                .build();
    }
}
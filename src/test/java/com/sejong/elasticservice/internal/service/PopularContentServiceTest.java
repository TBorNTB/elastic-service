package com.sejong.elasticservice.internal.service;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.internal.dto.PopularContentResponse;
import com.sejong.elasticservice.news.domain.Content;
import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
class PopularContentServiceTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private PopularContentService popularContentService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @BeforeEach
    void setUp() {
        // 테스트 전 기존 데이터 삭제
        // 인덱스는 애플리케이션 시작 시 자동 생성되므로 여기서는 데이터만 정리
        // (주의: 애플리케이션을 먼저 실행하여 인덱스가 생성되어 있어야 합니다)
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
        PopularContentResponse result = popularContentService.getMostPopularContent();

        // 결과 출력
        System.out.println("\n========== 인기글 조회 결과 ==========");
        if (result != null) {
            System.out.println("타입: " + result.getContentType());
            System.out.println("제목: " + result.getTitle());
            System.out.println("좋아요: " + result.getLikeCount());
            System.out.println("조회수: " + result.getViewCount());
            System.out.println("인기도 점수: " + result.calculatePopularityScore());
            System.out.println("생성일: " + result.getCreatedAt());
        } else {
            System.out.println("결과 없음");
        }
        System.out.println("=====================================\n");

        // 예상 결과: 프로젝트 E (인기도 700)
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
        PopularContentResponse result = popularContentService.getMostPopularContent();

        // 결과 출력
        System.out.println("\n========== 100개 초과 테스트 결과 ==========");
        if (result != null) {
            System.out.println("제목: " + result.getTitle());
            System.out.println("좋아요: " + result.getLikeCount());
            System.out.println("조회수: " + result.getViewCount());
            System.out.println("인기도 점수: " + result.calculatePopularityScore());
        }
        System.out.println("==========================================\n");

        // 예상: "슈퍼 인기 프로젝트" 선택 (Script Score Query가 제대로 동작하면)
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
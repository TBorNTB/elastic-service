package com.sejong.elasticservice.internal.service;

import com.sejong.elasticservice.common.constants.TechCategory;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.internal.dto.ContentResponse;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class InterestContentServiceTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private InterestContentService interestContentService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @BeforeEach
    void setUp() {
        // 테스트 전 기존 데이터 삭제
        try {
            // 모든 인덱스의 모든 문서 삭제
            elasticsearchOperations.indexOps(ProjectDocument.class).delete();
            elasticsearchOperations.indexOps(CsKnowledgeDocument.class).delete();

            // 인덱스 재생성 (매핑은 Document 클래스에서 자동 생성)
            elasticsearchOperations.indexOps(ProjectDocument.class).create();
            elasticsearchOperations.indexOps(CsKnowledgeDocument.class).create();

            Thread.sleep(1000); // 인덱스 생성 대기
        } catch (Exception e) {
            System.out.println("Setup error (ignored): " + e.getMessage());
        }
    }

    @Test
    void 단일_카테고리_랜덤_콘텐츠_조회() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // SYSTEM_HACKING 카테고리의 Project 데이터 생성
        List<ProjectDocument> projects = List.of(
                createProject("1", "시스템 해킹 프로젝트 A", now.minusDays(1), List.of("SYSTEM_HACKING")),
                createProject("2", "시스템 해킹 프로젝트 B", now.minusDays(2), List.of("SYSTEM_HACKING")),
                createProject("3", "시스템 해킹 프로젝트 C", now.minusDays(3), List.of("SYSTEM_HACKING"))
        );

        // SYSTEM_HACKING 카테고리의 CsKnowledge 데이터 생성
        List<CsKnowledgeDocument> csKnowledges = List.of(
                createCsKnowledge("1", "시스템 해킹 CS A", now.minusDays(1), "SYSTEM_HACKING"),
                createCsKnowledge("2", "시스템 해킹 CS B", now.minusDays(2), "SYSTEM_HACKING")
        );

        // 데이터 저장
        projects.forEach(elasticsearchOperations::save);
        csKnowledges.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // SYSTEM_HACKING 카테고리의 랜덤 컨텐츠 조회
        List<ContentResponse> results = interestContentService.getRandomContentsOf(
                List.of(TechCategory.SYSTEM_HACKING)
        );

        // 검증
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());

        ContentResponse result = results.get(0);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContentType().equals("PROJECT") || result.getContentType().equals("CS-KNOWLEDGE")
        );
        Assertions.assertTrue(result.getTitle().contains("시스템 해킹"));
    }

    @Test
    void 복수_카테고리_랜덤_콘텐츠_조회() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // 다양한 카테고리의 Project 데이터
        List<ProjectDocument> projects = List.of(
                createProject("1", "시스템 해킹 프로젝트", now.minusDays(1), List.of("SYSTEM_HACKING")),
                createProject("2", "웹 해킹 프로젝트", now.minusDays(2), List.of("WEB_HACKING")),
                createProject("3", "암호학 프로젝트", now.minusDays(3), List.of("CRYPTOGRAPHY"))
        );

        // 다양한 카테고리의 CsKnowledge 데이터
        List<CsKnowledgeDocument> csKnowledges = List.of(
                createCsKnowledge("1", "시스템 해킹 지식", now.minusDays(1), "SYSTEM_HACKING"),
                createCsKnowledge("2", "웹 해킹 지식", now.minusDays(2), "WEB_HACKING"),
                createCsKnowledge("3", "암호학 지식", now.minusDays(3), "CRYPTOGRAPHY")
        );

        // 데이터 저장
        projects.forEach(elasticsearchOperations::save);
        csKnowledges.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // 3개 카테고리의 랜덤 컨텐츠 조회
        List<ContentResponse> results = interestContentService.getRandomContentsOf(
                List.of(TechCategory.SYSTEM_HACKING, TechCategory.WEB_HACKING, TechCategory.CRYPTOGRAPHY)
        );

        // 검증
        Assertions.assertNotNull(results);
        Assertions.assertEquals(3, results.size());

        // 각 결과가 유효한지 확인
        results.forEach(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertNotNull(result.getTitle());
            Assertions.assertTrue(
                    result.getContentType().equals("PROJECT") || result.getContentType().equals("CS-KNOWLEDGE")
            );
        });
    }

    @Test
    void testGetRandomContentsWithOnlyProjects() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // NETWORK_SECURITY 카테고리의 Project만 존재
        List<ProjectDocument> projects = List.of(
                createProject("1", "네트워크 보안 프로젝트 A", now.minusDays(1), List.of("NETWORK_SECURITY")),
                createProject("2", "네트워크 보안 프로젝트 B", now.minusDays(2), List.of("NETWORK_SECURITY"))
        );

        // 데이터 저장
        projects.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // NETWORK_SECURITY 카테고리의 랜덤 컨텐츠 조회
        List<ContentResponse> results = interestContentService.getRandomContentsOf(
                List.of(TechCategory.NETWORK_SECURITY)
        );

        // 검증: CsKnowledge가 없어도 Project에서 선택되어야 함
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("PROJECT", results.get(0).getContentType());
        Assertions.assertTrue(results.get(0).getTitle().contains("네트워크 보안"));
    }

    @Test
    void testGetRandomContentsWithOnlyCsKnowledge() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // REVERSING 카테고리의 CsKnowledge만 존재
        List<CsKnowledgeDocument> csKnowledges = List.of(
                createCsKnowledge("1", "리버싱 지식 A", now.minusDays(1), "REVERSING"),
                createCsKnowledge("2", "리버싱 지식 B", now.minusDays(2), "REVERSING")
        );

        // 데이터 저장
        csKnowledges.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // REVERSING 카테고리의 랜덤 컨텐츠 조회
        List<ContentResponse> results = interestContentService.getRandomContentsOf(
                List.of(TechCategory.REVERSING)
        );

        // 검증: Project가 없어도 CsKnowledge에서 선택되어야 함
        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("CS-KNOWLEDGE", results.get(0).getContentType());
        Assertions.assertTrue(results.get(0).getTitle().contains("리버싱"));
    }

    @Test
    void testGetRandomContentsWithNoData() throws InterruptedException {
        // 데이터 없이 바로 조회
        Thread.sleep(1000);

        // IOT_SECURITY 카테고리의 랜덤 컨텐츠 조회 (데이터 없음)
        List<ContentResponse> results = interestContentService.getRandomContentsOf(
                List.of(TechCategory.IOT_SECURITY)
        );

        // 검증: 데이터가 없으면 빈 리스트 반환
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void testGetRandomContentsWithMixedData() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // DIGITAL_FORENSICS 카테고리: Project와 CsKnowledge 모두 존재
        List<ProjectDocument> projects = List.of(
                createProject("1", "디지털 포렌식 프로젝트 A", now.minusDays(1), List.of("DIGITAL_FORENSICS")),
                createProject("2", "디지털 포렌식 프로젝트 B", now.minusDays(2), List.of("DIGITAL_FORENSICS")),
                createProject("3", "디지털 포렌식 프로젝트 C", now.minusDays(3), List.of("DIGITAL_FORENSICS"))
        );

        List<CsKnowledgeDocument> csKnowledges = List.of(
                createCsKnowledge("1", "디지털 포렌식 지식 A", now.minusDays(1), "DIGITAL_FORENSICS"),
                createCsKnowledge("2", "디지털 포렌식 지식 B", now.minusDays(2), "DIGITAL_FORENSICS"),
                createCsKnowledge("3", "디지털 포렌식 지식 C", now.minusDays(3), "DIGITAL_FORENSICS")
        );

        // 데이터 저장
        projects.forEach(elasticsearchOperations::save);
        csKnowledges.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // 여러 번 호출하여 랜덤성 확인
        for (int i = 0; i < 5; i++) {
            List<ContentResponse> results = interestContentService.getRandomContentsOf(
                    List.of(TechCategory.DIGITAL_FORENSICS)
            );

            Assertions.assertNotNull(results);
            Assertions.assertEquals(1, results.size());

            ContentResponse result = results.get(0);
            Assertions.assertTrue(result.getTitle().contains("디지털 포렌식"));
            System.out.println("Iteration " + i + ": " + result.getContentType() + " - " + result.getTitle());
        }
    }

    @Test
    void testGetRandomContentsWithMultipleCategoriesInProject() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // 복수의 카테고리를 가진 Project
        List<ProjectDocument> projects = List.of(
                createProject("1", "복합 보안 프로젝트 A", now.minusDays(1),
                        List.of("SYSTEM_HACKING", "WEB_HACKING")),
                createProject("2", "복합 보안 프로젝트 B", now.minusDays(2),
                        List.of("WEB_HACKING", "NETWORK_SECURITY"))
        );

        // 데이터 저장
        projects.forEach(elasticsearchOperations::save);

        // Elasticsearch 인덱싱 대기
        Thread.sleep(2000);

        // SYSTEM_HACKING과 WEB_HACKING 카테고리 조회
        List<ContentResponse> results = interestContentService.getRandomContentsOf(
                List.of(TechCategory.SYSTEM_HACKING, TechCategory.WEB_HACKING)
        );

        // 검증: 2개 카테고리에 대해 결과가 반환되어야 함
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.size() >= 1); // 최소 1개 이상

        results.forEach(result -> {
            Assertions.assertTrue(result.getTitle().contains("복합 보안"));
        });
    }

    private ProjectDocument createProject(String id, String title, LocalDateTime createdAt, List<String> categories) {
        return ProjectDocument.builder()
                .id(id)
                .title(title)
                .description("프로젝트 설명: " + title)
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .projectCategories(categories)
                .createdAt(createdAt.format(FORMATTER))
                .updatedAt(createdAt.format(FORMATTER))
                .likeCount(10L)
                .viewCount(100L)
                .build();
    }

    private CsKnowledgeDocument createCsKnowledge(String id, String title, LocalDateTime createdAt, String category) {
        return CsKnowledgeDocument.builder()
                .id(id)
                .title(title)
                .content("CS 지식 내용: " + title)
                .category(category)
                .createdAt(createdAt.format(FORMATTER))
                .likeCount(5L)
                .viewCount(50L)
                .build();
    }
}

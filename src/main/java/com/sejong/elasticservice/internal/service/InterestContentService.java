package com.sejong.elasticservice.internal.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.sejong.elasticservice.common.constants.TechCategory;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.internal.dto.ContentResponse;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestContentService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final Random random = new Random();

    public List<ContentResponse> getRandomContentsOf(List<TechCategory> categories) {
        List<ContentResponse> results = new ArrayList<>();

        for (TechCategory category : categories) {
            // 각 카테고리별로 Project와 CsKnowledge 중 랜덤하게 하나 선택
            boolean searchProject = random.nextBoolean();

            if (searchProject) {
                ContentResponse projectContent = getRandomProjectByCategory(category);
                if (projectContent != null) {
                    results.add(projectContent);
                } else {
                    // Project가 없으면 CsKnowledge에서 시도
                    ContentResponse csContent = getRandomCsKnowledgeByCategory(category);
                    if (csContent != null) {
                        results.add(csContent);
                    }
                }
            } else {
                ContentResponse csContent = getRandomCsKnowledgeByCategory(category);
                if (csContent != null) {
                    results.add(csContent);
                } else {
                    // CsKnowledge가 없으면 Project에서 시도
                    ContentResponse projectContent = getRandomProjectByCategory(category);
                    if (projectContent != null) {
                        results.add(projectContent);
                    }
                }
            }
        }

        return results;
    }

    private ContentResponse getRandomProjectByCategory(TechCategory category) {
        try {
            String categoryName = category.name();

            // projectCategories.keyword 필드에 해당 카테고리가 포함된 문서 검색 (text 타입의 keyword 서브필드 사용)
            Query termQuery = Query.of(q -> q
                    .term(t -> t
                            .field("projectCategories.keyword")
                            .value(categoryName)
                    )
            );

            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(termQuery)
                    .withMaxResults(10000)  // 모든 결과 가져오기
                    .build();

            SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(
                    searchQuery, ProjectDocument.class);

            if (searchHits.isEmpty()) {
                log.debug("No projects found for category: {}", categoryName);
                return null;
            }

            List<ProjectDocument> projects = searchHits.stream()
                    .map(SearchHit::getContent)
                    .toList();

            // 랜덤하게 하나 선택
            ProjectDocument randomProject = projects.get(random.nextInt(projects.size()));
            log.info("Selected random project '{}' for category: {}", randomProject.getTitle(), categoryName);

            return ContentResponse.fromProject(randomProject);
        } catch (Exception e) {
            log.error("Error getting random project for category: {}", category, e);
            return null;
        }
    }

    private ContentResponse getRandomCsKnowledgeByCategory(TechCategory category) {
        try {
            String categoryName = category.name();

            // category.keyword 필드가 해당 카테고리와 정확히 일치하는 문서 검색 (text 타입의 keyword 서브필드 사용)
            Query termQuery = Query.of(q -> q
                    .term(t -> t
                            .field("category.keyword")
                            .value(categoryName)
                    )
            );

            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(termQuery)
                    .withMaxResults(10000)  // 모든 결과 가져오기
                    .build();

            SearchHits<CsKnowledgeDocument> searchHits = elasticsearchOperations.search(
                    searchQuery, CsKnowledgeDocument.class);

            if (searchHits.isEmpty()) {
                log.debug("No CS knowledge found for category: {}", categoryName);
                return null;
            }

            List<CsKnowledgeDocument> csKnowledges = searchHits.stream()
                    .map(SearchHit::getContent)
                    .toList();

            // 랜덤하게 하나 선택
            CsKnowledgeDocument randomCs = csKnowledges.get(random.nextInt(csKnowledges.size()));
            log.info("Selected random CS knowledge '{}' for category: {}", randomCs.getTitle(), categoryName);

            return ContentResponse.fromCsKnowledge(randomCs);
        } catch (Exception e) {
            log.error("Error getting random CS knowledge for category: {}", category, e);
            return null;
        }
    }
}

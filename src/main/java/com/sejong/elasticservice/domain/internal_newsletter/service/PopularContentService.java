package com.sejong.elasticservice.domain.internal_newsletter.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.internal_newsletter.dto.ContentResponse;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularContentService {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public ContentResponse getMostPopularContent() {
        // 테스트 데이터와 동일한 형식 사용
        String oneWeekAgo = LocalDateTime.now().minusWeeks(1).format(FORMATTER);

        List<ContentResponse> allContent = new ArrayList<>();

        // Project 검색
        List<ProjectDocument> projects = searchWeeklyProjects(oneWeekAgo);
        projects.forEach(project ->
            allContent.add(ContentResponse.fromProject(project))
        );

        // News 검색
        List<NewsDocument> news = searchWeeklyNews(oneWeekAgo);
        news.forEach(newsItem ->
            allContent.add(ContentResponse.fromNews(newsItem))
        );

        // CsKnowledge 검색
        List<CsKnowledgeDocument> csKnowledges = searchWeeklyCsKnowledge(oneWeekAgo);
        csKnowledges.forEach(cs ->
            allContent.add(ContentResponse.fromCsKnowledge(cs))
        );

        // 인기도 점수 계산하여 가장 높은 1개 반환 (좋아요 * 2 + 조회수). 같을 시 임의의 컨텐츠 반환
        return allContent.stream()
                .max(Comparator.comparingDouble(ContentResponse::calculatePopularityScore))
                .orElse(null);
    }

    private List<ProjectDocument> searchWeeklyProjects(String oneWeekAgo) {
        try {
            log.info("Searching projects from: {}", oneWeekAgo);

            // createdAt >= oneWeekAgo 조건
            Query rangeQuery = Query.of(q -> q
                    .range(r -> r
                            .date(d -> d
                                    .field("createdAt")
                                    .gte(oneWeekAgo)
                            )
                    )
            );

            // Elasticsearch의 Script Sort를 사용하여 정렬
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
                    .withPageable(PageRequest.of(0, 1))  // 상위 1개만
                    .build();

            SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(searchQuery, ProjectDocument.class);
            log.info("Found {} projects, returning top 1", searchHits.getTotalHits());

            List<ProjectDocument> results = searchHits.stream()
                    .map(SearchHit::getContent)
                    .toList();

            results.forEach(doc -> log.info("Selected project: {} (like={}, view={}, score={})",
                doc.getTitle(), doc.getLikeCount(), doc.getViewCount(),
                doc.getLikeCount() * 2 + doc.getViewCount()));

            return results;
        } catch (Exception e) {
            log.error("Error searching weekly projects", e);
            return new ArrayList<>();
        }
    }

    private List<NewsDocument> searchWeeklyNews(String oneWeekAgo) {
        try {
            // createdAt >= oneWeekAgo 조건
            Query rangeQuery = Query.of(q -> q
                    .range(r -> r
                            .date(d -> d
                                    .field("createdAt")
                                    .gte(oneWeekAgo)
                            )
                    )
            );

            // Elasticsearch의 Script Sort를 사용하여 정렬
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
                    .withPageable(PageRequest.of(0, 1))  // 상위 1개만
                    .build();

            SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(searchQuery, NewsDocument.class);

            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .toList();
        } catch (Exception e) {
            log.error("Error searching weekly news", e);
            return new ArrayList<>();
        }
    }

    private List<CsKnowledgeDocument> searchWeeklyCsKnowledge(String oneWeekAgo) {
        try {
            // createdAt >= oneWeekAgo 조건
            Query rangeQuery = Query.of(q -> q
                    .range(r -> r
                            .date(d -> d
                                    .field("createdAt")
                                    .gte(oneWeekAgo)
                            )
                    )
            );

            // Elasticsearch의 Script Sort를 사용하여 정렬
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
                    .withPageable(PageRequest.of(0, 1))  // 상위 1개만
                    .build();

            SearchHits<CsKnowledgeDocument> searchHits = elasticsearchOperations.search(searchQuery, CsKnowledgeDocument.class);

            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .toList();
        } catch (Exception e) {
            log.error("Error searching weekly cs knowledge", e);
            return new ArrayList<>();
        }
    }
}
package com.sejong.elasticservice.internal.service;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptScoreQuery;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.internal.dto.PopularContentResponse;
import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularContentService {

    private final ElasticsearchOperations elasticsearchOperations;

    public PopularContentResponse getMostPopularContent() {
        String oneWeekAgo = LocalDateTime.now().minusWeeks(1)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        List<PopularContentResponse> allContent = new ArrayList<>();

        // Project 검색
        List<ProjectDocument> projects = searchWeeklyProjects(oneWeekAgo);
        projects.forEach(project ->
            allContent.add(PopularContentResponse.fromProject(project))
        );

        // News 검색
        List<NewsDocument> news = searchWeeklyNews(oneWeekAgo);
        news.forEach(newsItem ->
            allContent.add(PopularContentResponse.fromNews(newsItem))
        );

        // CsKnowledge 검색
        List<CsKnowledgeDocument> csKnowledges = searchWeeklyCsKnowledge(oneWeekAgo);
        csKnowledges.forEach(cs ->
            allContent.add(PopularContentResponse.fromCsKnowledge(cs))
        );

        // 인기도 점수 계산하여 가장 높은 1개 반환 (좋아요 * 2 + 조회수). 같을 시 임의의 컨텐츠 반환
        return allContent.stream()
                .max(Comparator.comparingDouble(PopularContentResponse::calculatePopularityScore))
                .orElse(null);
    }

    private List<ProjectDocument> searchWeeklyProjects(String oneWeekAgo) {
        try {
            Query rangeQuery = RangeQuery.of(r -> r
                    .date(d -> d
                            .field("createdAt")
                            .gte(oneWeekAgo)
                    )
            )._toQuery();

            // Script Score Query: likeCount * 2 + viewCount
            Query scriptScoreQuery = ScriptScoreQuery.of(s -> s
                    .query(rangeQuery)
                    .script(Script.of(sc -> sc
                            .source("(doc['likeCount'].size() == 0 ? 0 : doc['likeCount'].value) * 2 + (doc['viewCount'].size() == 0 ? 0 : doc['viewCount'].value)")
                    ))
            )._toQuery();

            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(scriptScoreQuery)
                    .withPageable(PageRequest.of(0, 1))
                    .build();

            SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(searchQuery, ProjectDocument.class);

            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .toList();
        } catch (Exception e) {
            log.error("Error searching weekly projects", e);
            return new ArrayList<>();
        }
    }

    private List<NewsDocument> searchWeeklyNews(String oneWeekAgo) {
        try {
            Query rangeQuery = RangeQuery.of(r -> r
                    .date(d -> d
                            .field("createdAt")
                            .gte(oneWeekAgo)
                    )
            )._toQuery();

            // Script Score Query: likeCount * 2 + viewCount
            Query scriptScoreQuery = ScriptScoreQuery.of(s -> s
                    .query(rangeQuery)
                    .script(Script.of(sc -> sc
                            .source("(doc['likeCount'].size() == 0 ? 0 : doc['likeCount'].value) * 2 + (doc['viewCount'].size() == 0 ? 0 : doc['viewCount'].value)")
                    ))
            )._toQuery();

            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(scriptScoreQuery)
                    .withPageable(PageRequest.of(0, 1))
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
            Query rangeQuery = RangeQuery.of(r -> r
                    .date(d -> d
                            .field("createdAt")
                            .gte(oneWeekAgo)
                    )
            )._toQuery();

            // Script Score Query: likeCount * 2 + viewCount
            Query scriptScoreQuery = ScriptScoreQuery.of(s -> s
                    .query(rangeQuery)
                    .script(Script.of(sc -> sc
                            .source("(doc['likeCount'].size() == 0 ? 0 : doc['likeCount'].value) * 2 + (doc['viewCount'].size() == 0 ? 0 : doc['viewCount'].value)")
                    ))
            )._toQuery();

            NativeQuery searchQuery = NativeQuery.builder()
                    .withQuery(scriptScoreQuery)
                    .withPageable(PageRequest.of(0, 1))
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
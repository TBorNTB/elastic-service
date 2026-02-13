package com.sejong.elasticservice.domain.content.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.sejong.elasticservice.common.dto.UserInfo;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.content.dto.PostRequest;
import com.sejong.elasticservice.domain.content.dto.PostSummaryDto;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeDocumentRepository;
import com.sejong.elasticservice.domain.internal_newsletter.dto.ContentResponse;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.news.repository.NewsDocumentRepository;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import com.sejong.elasticservice.domain.project.repository.ProjectElasticDocumentRepository;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BFFService {

    private final ProjectElasticDocumentRepository projectRepository;
    private final NewsDocumentRepository newsRepository;
    private final CsKnowledgeDocumentRepository csKnowledgeRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");


    public List<PostSummaryDto> getPostsByTypeAndIds(PostRequest request) {
        List<PostRequest.PostItem> posts = request.getPosts();

        // postType별로 그룹화
        Map<PostType, List<Long>> groupedByType = posts.stream()
                .collect(Collectors.groupingBy(
                        PostRequest.PostItem::getPostType,
                        Collectors.mapping(PostRequest.PostItem::getPostId, Collectors.toList())
                ));

        List<PostSummaryDto> result = new ArrayList<>();

        // PROJECT 처리
        if (groupedByType.containsKey(PostType.PROJECT)) {
            List<String> projectIds = groupedByType.get(PostType.PROJECT).stream()
                    .map(String::valueOf)
                    .toList();
            Iterable<ProjectDocument> documents = projectRepository.findAllById(projectIds);
            List<ProjectDocument> documentList = new ArrayList<>();
            documents.forEach(documentList::add);
            result.addAll(documentList.stream()
                    .map(doc -> PostSummaryDto.builder()
                            .postId(Long.parseLong(doc.getId()))
                            .postType(PostType.PROJECT)
                            .title(doc.getTitle())
                            .createdAt(doc.getCreatedAt())
                            .writer(UserInfo.from(doc.getOwner()))
                            .build())
                    .toList());
        }

        // NEWS 처리
        if (groupedByType.containsKey(PostType.NEWS)) {
            List<String> newsIds = groupedByType.get(PostType.NEWS).stream()
                    .map(String::valueOf)
                    .toList();
            Iterable<NewsDocument> documents = newsRepository.findAllById(newsIds);
            List<NewsDocument> documentList = new ArrayList<>();
            documents.forEach(documentList::add);
            result.addAll(documentList.stream()
                    .map(doc -> PostSummaryDto.builder()
                            .postId(Long.parseLong(doc.getId()))
                            .postType(PostType.NEWS)
                            .title(doc.getContent() != null ? doc.getContent().getTitle() : null)
                            .createdAt(doc.getCreatedAt())
                            .writer(UserInfo.from(doc.getWriter()))
                            .build())
                    .toList());
        }

        // CSKNOWLEDGE 처리
        if (groupedByType.containsKey(PostType.ARTICLE)) {
            List<String> csIds = groupedByType.get(PostType.ARTICLE).stream()
                    .map(String::valueOf)
                    .toList();
            Iterable<CsKnowledgeDocument> documents = csKnowledgeRepository.findAllById(csIds);
            List<CsKnowledgeDocument> documentList = new ArrayList<>();
            documents.forEach(documentList::add);
            result.addAll(documentList.stream()
                    .map(doc -> PostSummaryDto.builder()
                            .postId(Long.parseLong(doc.getId()))
                            .postType(PostType.ARTICLE)
                            .title(doc.getTitle())
                            .createdAt(doc.getCreatedAt())
                            .writer(UserInfo.from(doc.getWriter()))
                            .build())
                    .toList());
        }

        return result;
    }

    public PageResponse<ContentResponse> getPopularContents(int page, int size) {
        List<ContentResponse> allContent = new ArrayList<>();

        // Project 검색 - 인기도 순으로 정렬하여 최상위 3개만 가져오기
        List<ProjectDocument> projects = searchPopularProjects(3);
        projects.forEach(project ->
            allContent.add(ContentResponse.fromProject(project))
        );

        // News 검색 - 인기도 순으로 정렬하여 최상위 3개만 가져오기
        List<NewsDocument> news = searchPopularNews(3);
        news.forEach(newsItem ->
            allContent.add(ContentResponse.fromNews(newsItem))
        );

        // CsKnowledge 검색 - 인기도 순으로 정렬하여 최상위 3개만 가져오기
        List<CsKnowledgeDocument> csKnowledges = searchPopularCsKnowledge(3);
        csKnowledges.forEach(cs ->
            allContent.add(ContentResponse.fromCsKnowledge(cs))
        );

        // 인기도 점수로 정렬 (내림차순)
        List<ContentResponse> sortedContent = allContent.stream()
            .sorted(Comparator.comparingDouble(ContentResponse::calculatePopularityScore).reversed())
            .toList();

        // 페이지네이션 적용
        int start = page * size;
        int end = Math.min(start + size, sortedContent.size());
        List<ContentResponse> pagedContent = start < sortedContent.size()
            ? sortedContent.subList(start, end)
            : new ArrayList<>();

        int totalPages = (int) Math.ceil((double) sortedContent.size() / size);

        return new PageResponse<>(
            pagedContent,
            page,
            size,
            sortedContent.size(),
            totalPages
        );
    }

    private List<ProjectDocument> searchPopularProjects(int size) {
        try {
            Query matchAllQuery = MatchAllQuery.of(m -> m)._toQuery();

            // Elasticsearch의 Script Sort를 사용하여 인기도 순으로 정렬
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
                .withQuery(matchAllQuery)
                .withSort(List.of(scriptSort))
                .withPageable(PageRequest.of(0, size))
                .build();

            SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(searchQuery, ProjectDocument.class);

            return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        } catch (Exception e) {
            log.error("Error searching popular projects", e);
            return new ArrayList<>();
        }
    }

    private List<NewsDocument> searchPopularNews(int size) {
        try {
            Query matchAllQuery = MatchAllQuery.of(m -> m)._toQuery();

            // Elasticsearch의 Script Sort를 사용하여 인기도 순으로 정렬
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
                .withQuery(matchAllQuery)
                .withSort(List.of(scriptSort))
                .withPageable(PageRequest.of(0, size))
                .build();

            SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(searchQuery, NewsDocument.class);

            return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        } catch (Exception e) {
            log.error("Error searching popular news", e);
            return new ArrayList<>();
        }
    }

    private List<CsKnowledgeDocument> searchPopularCsKnowledge(int size) {
        try {
            Query matchAllQuery = MatchAllQuery.of(m -> m)._toQuery();

            // Elasticsearch의 Script Sort를 사용하여 인기도 순으로 정렬
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
                .withQuery(matchAllQuery)
                .withSort(List.of(scriptSort))
                .withPageable(PageRequest.of(0, size))
                .build();

            SearchHits<CsKnowledgeDocument> searchHits = elasticsearchOperations.search(searchQuery, CsKnowledgeDocument.class);

            return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        } catch (Exception e) {
            log.error("Error searching popular cs knowledge", e);
            return new ArrayList<>();
        }
    }

    public PageResponse<ContentResponse> getUserLatestContents(String username, int page, int size) {
        List<ContentResponse> allContent = new ArrayList<>();

        // Project 검색 - username으로 검색하고 최신순 정렬
        List<ProjectDocument> projects = searchProjectsByUsername(username, size * 3);
        projects.forEach(project ->
            allContent.add(ContentResponse.fromProject(project))
        );

        // News 검색 - username으로 검색하고 최신순 정렬
        List<NewsDocument> news = searchNewsByUsername(username, size * 3);
        news.forEach(newsItem ->
            allContent.add(ContentResponse.fromNews(newsItem))
        );

        // CsKnowledge 검색 - username으로 검색하고 최신순 정렬
        List<CsKnowledgeDocument> csKnowledges = searchCsKnowledgeByUsername(username, size * 3);
        csKnowledges.forEach(cs ->
            allContent.add(ContentResponse.fromCsKnowledge(cs))
        );

        // createdAt으로 정렬 (내림차순 - 최신순)
        List<ContentResponse> sortedContent = allContent.stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .toList();

        // 페이지네이션 적용
        int start = page * size;
        int end = Math.min(start + size, sortedContent.size());
        List<ContentResponse> pagedContent = start < sortedContent.size()
            ? sortedContent.subList(start, end)
            : new ArrayList<>();

        int totalPages = (int) Math.ceil((double) sortedContent.size() / size);

        return new PageResponse<>(
            pagedContent,
            page,
            size,
            sortedContent.size(),
            totalPages
        );
    }

    private List<ProjectDocument> searchProjectsByUsername(String username, int size) {
        try {
            Query boolQuery = BoolQuery.of(b -> b
                .should(
                    TermQuery.of(t -> t.field("owner.username").value(username))._toQuery(),
                    TermQuery.of(t -> t.field("collaborators.username").value(username))._toQuery()
                )
                .minimumShouldMatch("1")
            )._toQuery();

            NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
                .withPageable(PageRequest.of(0, size))
                .build();

            SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(searchQuery, ProjectDocument.class);

            return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        } catch (Exception e) {
            log.error("Error searching projects by username", e);
            return new ArrayList<>();
        }
    }

    private List<NewsDocument> searchNewsByUsername(String username, int size) {
        try {
            Query boolQuery = BoolQuery.of(b -> b
                .should(
                    TermQuery.of(t -> t.field("writer.username").value(username))._toQuery(),
                    TermQuery.of(t -> t.field("participants.username").value(username))._toQuery()
                )
                .minimumShouldMatch("1")
            )._toQuery();

            NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
                .withPageable(PageRequest.of(0, size))
                .build();

            SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(searchQuery, NewsDocument.class);

            return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        } catch (Exception e) {
            log.error("Error searching news by username", e);
            return new ArrayList<>();
        }
    }

    private List<CsKnowledgeDocument> searchCsKnowledgeByUsername(String username, int size) {
        try {
            Query boolQuery = BoolQuery.of(b -> b
                .must(TermQuery.of(t -> t.field("writer.username").value(username))._toQuery())
            )._toQuery();

            NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
                .withPageable(PageRequest.of(0, size))
                .build();

            SearchHits<CsKnowledgeDocument> searchHits = elasticsearchOperations.search(searchQuery, CsKnowledgeDocument.class);

            return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        } catch (Exception e) {
            log.error("Error searching cs knowledge by username", e);
            return new ArrayList<>();
        }
    }
}

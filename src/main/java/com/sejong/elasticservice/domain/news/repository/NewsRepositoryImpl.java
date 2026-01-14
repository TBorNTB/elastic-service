package com.sejong.elasticservice.domain.news.repository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;

import java.util.ArrayList;

import com.sejong.elasticservice.domain.project.domain.PostSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

    private final ElasticsearchOperations operations;
    private final String INDEX_NAME = "news";

    @Override
    public String save(NewsDocument newsDocument) {
        NewsDocument saved = operations.save(newsDocument, IndexCoordinates.of(INDEX_NAME));
        return saved.getId();
    }

    @Override
    public void deleteById(String newsId) {
        operations.delete(newsId, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public PageResponse<NewsDocument> searchNews(String keyword, String category, PostSortType postSortType, int page, int size) {

        Query multiMatchQuery = (keyword == null || keyword.isBlank())
                ? MatchAllQuery.of(m -> m)._toQuery()
                : MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("content.title^3", "content.summary^2", "content.content^1", "content.category^1")
                .fuzziness("AUTO")
        )._toQuery();

        List<Query> filters = new ArrayList<>();
        // term filter 쿼리 : 카테고리가 정확히 일치하는 것만 필터링
        if (category != null && !category.trim().isEmpty()) {
            Query categoryFilter = TermQuery.of(t -> t
                    .field("content.category.raw")
                    .value(category)
            )._toQuery();
            filters.add(categoryFilter);
        }

        // bool query 조합
        Query boolQuery = BoolQuery.of(b -> b
                .must(multiMatchQuery)
                .filter(filters)
        )._toQuery();

        Sort sort = switch (postSortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCount");
        };

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(sort)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);

        SearchPage<NewsDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));

        return new PageResponse<>(
                searchPage.getContent().stream()
                        .map(SearchHit::getContent)
                        .toList(),
                searchPage.getNumber(),
                searchPage.getSize(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
    }

    @Override
    public List<NewsDocument> searchNews(int page, int size) {
        Query matchAllQuery = MatchAllQuery.of(m -> m)._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(matchAllQuery)
                .withSort(Sort.by(Sort.Order.desc("createdAt")))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Override
    public List<NewsDocument> searchByTags(List<String> tags, int page, int size) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }

        List<FieldValue> tagValues = tags.stream()
                .map(FieldValue::of)
                .toList();

        Query termsQuery = TermsQuery.of(t -> t
                .field("tags")
                .terms(v -> v.value(tagValues))
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(termsQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Override
    public void updateLikeCount(Long newsId, Long likeCount) {
        Document document = Document.create();
        document.put("likeCount", likeCount);

        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(newsId))
                .withDocument(document)
                .build();

        operations.update(updateQuery, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public void updateViewCount(Long newsId, Long viewCount) {
        Document document = Document.create();
        document.put("viewCount", viewCount);

        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(newsId))
                .withDocument(document)
                .build();

        operations.update(updateQuery, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public List<String> getSuggestions(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .type(TextQueryType.BoolPrefix)
                .fields(
                        "content.title.auto_complete",
                        "content.title.auto_complete._2gram",
                        "content.title.auto_complete._3gram"
                )
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(multiMatchQuery)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    NewsDocument newsDocument = hit.getContent();
                    return newsDocument.getContent().getTitle();
                })
                .toList();
    }

    @Override
    public PageResponse<NewsDocument> searchByMemberName(String name, int size, int page) {
        Query boolQuery = BoolQuery.of(b -> b
                .should(
                        TermQuery.of(t -> t.field("writer.nickname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("writer.realname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("participants.nickname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("participants.realname").value(name))._toQuery()
                )
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);

        SearchPage<NewsDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));

        return new PageResponse<>(
                searchPage.getContent().stream()
                        .map(SearchHit::getContent)
                        .toList(),
                searchPage.getNumber(),
                searchPage.getSize(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
    }

    @Override
    public PageResponse<NewsDocument> searchByUsername(String username, int size, int page) {
        Query boolQuery = BoolQuery.of(b -> b
                .should(
                        TermQuery.of(t -> t.field("writer.username").value(username))._toQuery(),
                        TermQuery.of(t -> t.field("participants.username").value(username))._toQuery()
                )
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);

        SearchPage<NewsDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));

        return new PageResponse<>(
                searchPage.getContent().stream()
                        .map(SearchHit::getContent)
                        .toList(),
                searchPage.getNumber(),
                searchPage.getSize(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
    }
}
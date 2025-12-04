package com.sejong.elasticservice.news.repository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.news.domain.NewsEvent;
import com.sejong.elasticservice.news.dto.NewsSearchDto;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {

    private final NewsDocumentRepository repository;
    private final ElasticsearchOperations operations;
    private final String INDEX_NAME = "news";

    @Override
    public String save(NewsEvent newsEvent) {
        NewsDocument document = NewsDocument.from(newsEvent);
        NewsDocument saved = operations.save(document, IndexCoordinates.of(INDEX_NAME));
        return saved.getId();
    }

    @Override
    public void deleteById(String newsId) {
        operations.delete(newsId, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public List<NewsDocument> searchNews(String keyword, String category, int page, int size) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("content.title^3", "content.summary^2", "content.content^1", "content.category^1")
                .fuzziness("AUTO")
        )._toQuery();

        List<Query> filters = new ArrayList<>();
        // term filter 쿼리 : 카테고리가 정확히 일치하는 것만 필터링
        if (category != null && !category.isEmpty()) {
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

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page - 1, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Override
    public List<NewsDocument> searchNews(int page, int size) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withSort(Sort.by(Sort.Order.desc("createdAt")))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<NewsDocument> searchHits = operations.search(nativeQuery, NewsDocument.class);

        return searchHits.stream()
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
                .withPageable(PageRequest.of(page - 1, size))
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

}
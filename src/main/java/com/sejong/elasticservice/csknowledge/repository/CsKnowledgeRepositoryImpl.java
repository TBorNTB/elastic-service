package com.sejong.elasticservice.csknowledge.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeEvent;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class CsKnowledgeRepositoryImpl implements CsKnowledgeRepository {

    private final CsKnowledgeDocumentRepository repository;
    private final ElasticsearchOperations operations;
    private final String INDEX_NAME = "cs-knowledge";

    @Override
    public String save(CsKnowledgeEvent csKnowledgeEvent) {
        CsKnowledgeDocument document = CsKnowledgeDocument.from(csKnowledgeEvent);
        CsKnowledgeDocument saved = operations.save(document, IndexCoordinates.of(INDEX_NAME));
        return saved.getId();
    }

    @Override
    public void deleteById(String csKnowledgeId) {
        operations.delete(csKnowledgeId, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public List<String> getSuggestions(String query) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .type(TextQueryType.BoolPrefix)
                .fields("title.auto_complete", "title.auto_complete._2gram",
                        "title.auto_complete._3gram"))._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(multiMatchQuery)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<CsKnowledgeDocument> searchHits = operations.search(nativeQuery, CsKnowledgeDocument.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    CsKnowledgeDocument csKnowledgeDocument = hit.getContent();
                    return csKnowledgeDocument.getTitle();
                })
                .toList();
    }

    @Override
    public List<CsKnowledgeDocument> searchCsKnowledge(String keyword, String category, int page, int size) {
        Query textQuery = (keyword == null || keyword.isBlank())
                ? MatchAllQuery.of(m -> m)._toQuery()
                : MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("title^3", "content^2", "category^1")
                .fuzziness("AUTO")
        )._toQuery();

        List<Query> filters = new ArrayList<>();
        // term filter 쿼리 : 카테고리가 정확히 일치하는 것만 필터링
        if (category != null && !category.trim().isEmpty()) {
            Query categoryFilter = TermQuery.of(t -> t
                    .field("category.raw")
                    .value(category)
            )._toQuery();
            filters.add(categoryFilter);
        }

        // bool query 조합
        Query boolQuery = BoolQuery.of(b -> b
                .must(textQuery)
                .filter(filters)
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page - 1, size))
                .build();

        SearchHits<CsKnowledgeDocument> searchHits = operations.search(nativeQuery, CsKnowledgeDocument.class);
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Override
    public void updateLikeCount(Long csKnowledgeId, Long likeCount) {
        Document document = Document.create();
        document.put("likeCount", likeCount);
        
        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(csKnowledgeId))
                .withDocument(document)
                .build();
        
        operations.update(updateQuery, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public void updateViewCount(Long csKnowledgeId, Long viewCount) {
        Document document = Document.create();
        document.put("viewCount", viewCount);
        
        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(csKnowledgeId))
                .withDocument(document)
                .build();
        
        operations.update(updateQuery, IndexCoordinates.of(INDEX_NAME));
    }
}

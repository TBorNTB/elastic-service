package com.sejong.elasticservice.domain.csknowledge.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import java.util.ArrayList;
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
public class CsKnowledgeRepositoryImpl implements CsKnowledgeRepository {

    private final ElasticsearchOperations operations;
    private final String INDEX_NAME = "cs-knowledge";

    @Override
    public String save(CsKnowledgeDocument csKnowledgeDocument) {
        CsKnowledgeDocument saved = operations.save(csKnowledgeDocument, IndexCoordinates.of(INDEX_NAME));
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
    public PageResponse<CsKnowledgeDocument> searchCsKnowledge(String keyword, String category, PostSortType sortType, int page, int size) {
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

        Sort sort = switch (sortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCount");
        };

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(sort)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<CsKnowledgeDocument> searchHits = operations.search(nativeQuery, CsKnowledgeDocument.class);
        SearchPage<CsKnowledgeDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));

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

    @Override
    public PageResponse<CsKnowledgeDocument> searchByMemberName(String name, int size, int page) {
        Query boolQuery = BoolQuery.of(b -> b
                .should(
                        TermQuery.of(t -> t.field("writer.nickname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("writer.realname").value(name))._toQuery()
                )
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<CsKnowledgeDocument> searchHits = operations.search(nativeQuery, CsKnowledgeDocument.class);

        SearchPage<CsKnowledgeDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));

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

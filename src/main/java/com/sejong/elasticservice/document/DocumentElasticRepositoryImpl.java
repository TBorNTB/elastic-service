package com.sejong.projectservice.infrastructure.document.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.domain.DocumentDocument;
import com.sejong.projectservice.core.document.repository.DocumentElasticRepository;
import com.sejong.projectservice.infrastructure.document.entity.DocumentElastic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DocumentElasticRepositoryImpl implements DocumentElasticRepository {
    private final DocumentElasticDocumentRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void deleteById(Long documentId) {
        repository.deleteById(documentId.toString());
    }

    @Override
    public void save(Document savedDocument) {
        DocumentElastic documentElastic = DocumentElastic.from(savedDocument);
        repository.save(documentElastic);
    }

    @Override
    public List<String> getSuggestions(String query) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .type(TextQueryType.BoolPrefix)
                .fields("title.auto_complete", "title.auto_complete._2gram", "title.auto_complete._3gram")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(multiMatchQuery)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<DocumentElastic> searchHits = elasticsearchOperations.search(nativeQuery, DocumentElastic.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getTitle())
                .toList();
    }

    @Override
    public List<DocumentDocument> searchDocuments(String query, int size, int page) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .fields("title^3", "description^2", "content")
                .fuzziness("AUTO")
        )._toQuery();

        Query boolQuery = BoolQuery.of(b -> b
                .must(multiMatchQuery)
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<DocumentElastic> searchHits = elasticsearchOperations.search(nativeQuery, DocumentElastic.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(DocumentElastic::toDocument)
                .toList();
    }


}

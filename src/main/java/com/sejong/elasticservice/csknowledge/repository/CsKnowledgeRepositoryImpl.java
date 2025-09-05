package com.sejong.elasticservice.csknowledge.repository;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CsKnowledgeRepositoryImpl implements CsKnowledgeRepository {
    
    private final ElasticsearchOperations operations;
    private final String INDEX_NAME = "csknowledge";

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
    public List<CsKnowledgeEvent> searchByTitle(String keyword) {
        // TODO: Implement search logic
        return List.of();
    }

    @Override
    public List<CsKnowledgeEvent> searchByCategory(String category) {
        // TODO: Implement search logic
        return List.of();
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
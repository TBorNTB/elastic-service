package com.sejong.elasticservice.news.repository;

import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.news.domain.NewsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryImpl implements NewsRepository {
    
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
    public List<NewsEvent> searchByTitle(String keyword) {
        // TODO: Implement search logic
        return List.of();
    }

    @Override
    public List<NewsEvent> searchByTags(List<String> tags) {
        // TODO: Implement search logic
        return List.of();
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
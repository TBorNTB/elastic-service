package com.sejong.elasticservice.document;


import java.util.List;

public interface DocumentElasticRepository {
    void deleteById(String documentId);

    void save(DocumentDocument savedDocument);

    List<String> getSuggestions(String query);

    List<DocumentDocument> searchDocuments(String query, int size, int page);
}

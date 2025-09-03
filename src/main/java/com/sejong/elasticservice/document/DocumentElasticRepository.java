package com.sejong.elasticservice.document;


import java.util.List;

public interface DocumentElasticRepository {
    void deleteById(String documentId);

    void save(DocumentEvent savedDocument);

    List<String> getSuggestions(String query);

    List<DocumentEvent> searchDocuments(String query, int size, int page);
}

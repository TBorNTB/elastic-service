package com.sejong.elasticservice.domain.document.repository;


import com.sejong.elasticservice.domain.document.domain.DocumentDocument;
import com.sejong.elasticservice.domain.document.domain.DocumentEvent;
import java.util.List;

public interface DocumentRepository {
    void deleteById(String documentId);

    void save(DocumentEvent savedDocument);

    List<String> getSuggestions(String query);

    List<DocumentDocument> searchDocuments(String query, int size, int page);
}

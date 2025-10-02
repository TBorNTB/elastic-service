package com.sejong.elasticservice.document.repository;


import com.sejong.elasticservice.document.domain.DocumentDocument;
import com.sejong.elasticservice.document.domain.DocumentEvent;
import com.sejong.elasticservice.document.dto.DocumentSearchDto;
import java.util.List;

public interface DocumentRepository {
    void deleteById(String documentId);

    void save(DocumentEvent savedDocument);

    List<String> getSuggestions(String query);

    List<DocumentDocument> searchDocuments(String query, int size, int page);
}

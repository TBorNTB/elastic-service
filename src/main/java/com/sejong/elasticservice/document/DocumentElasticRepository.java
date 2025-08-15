package com.sejong.projectservice.core.document.repository;

import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.domain.DocumentDocument;

import java.util.List;

public interface DocumentElasticRepository {
    void deleteById(Long documentId);

    void save(Document savedDocument);

    List<String> getSuggestions(String query);

    List<DocumentDocument> searchDocuments(String query, int size, int page);
}

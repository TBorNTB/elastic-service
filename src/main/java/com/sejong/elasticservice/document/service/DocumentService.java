package com.sejong.elasticservice.document.service;

import com.sejong.elasticservice.document.domain.DocumentEvent;
import com.sejong.elasticservice.document.repository.DocumentElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentElasticRepository documentElasticRepository;

    public List<String> getSuggestions(String query) {
        return documentElasticRepository.getSuggestions(query);
    }

    public List<DocumentEvent> searchDocuments(String query, int size, int page) {
        return documentElasticRepository.searchDocuments(query,size,page);
    }

}

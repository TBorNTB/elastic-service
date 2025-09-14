package com.sejong.elasticservice.document.service;

import com.sejong.elasticservice.document.dto.DocumentSearchDto;
import com.sejong.elasticservice.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public List<String> getSuggestions(String query) {
        return documentRepository.getSuggestions(query);
    }

    public List<DocumentSearchDto> searchDocuments(String query, int size, int page) {
        return documentRepository.searchDocuments(query, size, page);
    }

}

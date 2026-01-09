package com.sejong.elasticservice.domain.document.service;

import com.sejong.elasticservice.domain.document.domain.DocumentDocument;
import com.sejong.elasticservice.domain.document.dto.DocumentSearchDto;
import com.sejong.elasticservice.domain.document.repository.DocumentRepository;
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
      List<DocumentDocument> documentDocuments = documentRepository.searchDocuments(query, size, page);
      return documentDocuments.stream().map(DocumentSearchDto::toDocumentSearchDto).toList();
    }

}

package com.sejong.elasticservice.csknowledge.service;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.csknowledge.dto.CsKnowledgeSearchDto;
import com.sejong.elasticservice.csknowledge.repository.CsKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CsKnowledgeService {

    private final CsKnowledgeRepository csKnowledgeRepository;

    public List<CsKnowledgeSearchDto> searchCsKnowledge(String keyword, String category, int page, int size) {
      List<CsKnowledgeDocument> csKnowledgeDocuments = csKnowledgeRepository.searchCsKnowledge(keyword, category, page, size);
      return csKnowledgeDocuments.stream().map(CsKnowledgeSearchDto::toCsKnowledgeSearchDto).toList();
    }
}
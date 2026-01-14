package com.sejong.elasticservice.domain.csknowledge.service;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.csknowledge.dto.CsKnowledgeSearchDto;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CsKnowledgeService {

    private final CsKnowledgeRepository csKnowledgeRepository;

    public PageResponse<CsKnowledgeSearchDto> searchCsKnowledge(String keyword, String category, PostSortType sortType, int page, int size) {
        PageResponse<CsKnowledgeDocument> result = csKnowledgeRepository.searchCsKnowledge(keyword, category, sortType, page, size);
        List<CsKnowledgeSearchDto> dtoList = result.content()
                .stream()
                .map(CsKnowledgeSearchDto::toCsKnowledgeSearchDto)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public List<String> getSuggestions(String query) {
        return csKnowledgeRepository.getSuggestions(query);
    }

    public PageResponse<CsKnowledgeSearchDto> searchByMemberName(String name, int size, int page) {
        PageResponse<CsKnowledgeDocument> result = csKnowledgeRepository.searchByMemberName(name, size, page);

        List<CsKnowledgeSearchDto> dtoList = result.content()
                .stream()
                .map(CsKnowledgeSearchDto::toCsKnowledgeSearchDto)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public PageResponse<CsKnowledgeSearchDto> searchByUsername(String username, int size, int page) {
        PageResponse<CsKnowledgeDocument> result = csKnowledgeRepository.searchByUsername(username, size, page);

        List<CsKnowledgeSearchDto> dtoList = result.content()
                .stream()
                .map(CsKnowledgeSearchDto::toCsKnowledgeSearchDto)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
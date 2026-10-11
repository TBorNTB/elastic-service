package com.sejong.elasticservice.domain.csknowledge.service;

import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.csknowledge.dto.CsKnowledgeSearchDto;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CsKnowledgeService {

    private final CsKnowledgeRepository csKnowledgeRepository;
    private final UserExternalService userExternalService;

    public PageResponse<CsKnowledgeSearchDto> searchCsKnowledge(String keyword, String category, PostSortType sortType, int page, int size) {
        PageResponse<CsKnowledgeDocument> result = csKnowledgeRepository.searchCsKnowledge(keyword, category, sortType, page, size);
        List<CsKnowledgeSearchDto> dtoList = toSearchDtos(result.content());

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

        List<CsKnowledgeSearchDto> dtoList = toSearchDtos(result.content());

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

        List<CsKnowledgeSearchDto> dtoList = toSearchDtos(result.content());

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    private List<CsKnowledgeSearchDto> toSearchDtos(List<CsKnowledgeDocument> documents) {
        if (documents.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = documents.stream()
                .map(document -> Long.parseLong(document.getId()))
                .toList();
        Map<Long, Long> commentCounts = userExternalService.getCommentCounts(PostType.ARTICLE, postIds);

        return documents.stream()
                .map(document -> {
                    Long postId = Long.parseLong(document.getId());
                    return CsKnowledgeSearchDto.toCsKnowledgeSearchDto(
                            document, commentCounts.getOrDefault(postId, 0L));
                })
                .toList();
    }
}

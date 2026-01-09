package com.sejong.elasticservice.domain.csknowledge.repository;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import java.util.List;

public interface CsKnowledgeRepository {
    String save(CsKnowledgeDocument csKnowledgeDocument);
    void deleteById(String csKnowledgeId);
    PageResponse<CsKnowledgeDocument> searchCsKnowledge(String keyword, String category, PostSortType sortType, int page, int size);
    List<String> getSuggestions(String query);
    void updateLikeCount(Long csKnowledgeId, Long likeCount);
    void updateViewCount(Long csKnowledgeId, Long viewCount);
    PageResponse<CsKnowledgeDocument> searchByMemberName(String name, int size, int page);
}
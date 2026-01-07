package com.sejong.elasticservice.csknowledge.repository;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeEvent;
import com.sejong.elasticservice.project.domain.PostSortType;
import java.util.List;

public interface CsKnowledgeRepository {
    String save(CsKnowledgeEvent csKnowledgeEvent);
    void deleteById(String csKnowledgeId);
    PageResponse<CsKnowledgeDocument> searchCsKnowledge(String keyword, String category, PostSortType sortType, int page, int size);
    List<String> getSuggestions(String query);
    void updateLikeCount(Long csKnowledgeId, Long likeCount);
    void updateViewCount(Long csKnowledgeId, Long viewCount);
}
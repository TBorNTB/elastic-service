package com.sejong.elasticservice.csknowledge.repository;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeEvent;
import java.util.List;

public interface CsKnowledgeRepository {
    String save(CsKnowledgeEvent csKnowledgeEvent);
    void deleteById(String csKnowledgeId);
    List<CsKnowledgeEvent> searchByTitle(String keyword);
    List<CsKnowledgeEvent> searchByCategory(String category);
    void updateLikeCount(Long csKnowledgeId, Long likeCount);
    void updateViewCount(Long csKnowledgeId, Long viewCount);
}
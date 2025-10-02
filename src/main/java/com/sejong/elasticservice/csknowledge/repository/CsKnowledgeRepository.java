package com.sejong.elasticservice.csknowledge.repository;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeEvent;
import com.sejong.elasticservice.csknowledge.dto.CsKnowledgeSearchDto;
import java.util.List;

public interface CsKnowledgeRepository {
    String save(CsKnowledgeEvent csKnowledgeEvent);
    void deleteById(String csKnowledgeId);
    List<CsKnowledgeDocument> searchCsKnowledge(String keyword, String category, int page, int size);
    void updateLikeCount(Long csKnowledgeId, Long likeCount);
    void updateViewCount(Long csKnowledgeId, Long viewCount);
}
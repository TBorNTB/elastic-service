package com.sejong.elasticservice.domain.csknowledge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.csknowledge.dto.CsKnowledgeSearchDto;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsKnowledgeServiceTest {

    @Mock
    private CsKnowledgeRepository csKnowledgeRepository;

    @Mock
    private UserExternalService userExternalService;

    @InjectMocks
    private CsKnowledgeService csKnowledgeService;

    @Test
    void 검색_결과에_게시물별_댓글_수를_포함한다() {
        CsKnowledgeDocument document = CsKnowledgeDocument.builder()
                .id("101")
                .title("AWS")
                .build();
        PageResponse<CsKnowledgeDocument> documents = new PageResponse<>(List.of(document), 0, 6, 1, 1);
        when(csKnowledgeRepository.searchCsKnowledge(null, null, PostSortType.LATEST, 0, 6))
                .thenReturn(documents);
        when(userExternalService.getCommentCounts(eq(PostType.ARTICLE), anyList()))
                .thenReturn(Map.of(101L, 3L));

        PageResponse<CsKnowledgeSearchDto> response = csKnowledgeService
                .searchCsKnowledge(null, null, PostSortType.LATEST, 0, 6);

        assertEquals(3L, response.content().getFirst().getCommentCount());
    }
}

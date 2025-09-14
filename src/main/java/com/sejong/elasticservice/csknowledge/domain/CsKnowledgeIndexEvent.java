package com.sejong.elasticservice.csknowledge.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.elasticservice.common.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private CsKnowledgeEvent csKnowledgeEvent;

    public static CsKnowledgeIndexEvent fromJson(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message, CsKnowledgeIndexEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패");
        }
    }
}
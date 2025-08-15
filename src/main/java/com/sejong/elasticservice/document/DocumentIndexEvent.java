package com.sejong.elasticservice.document;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.elasticservice.project.ProjectIndexEvent;
import com.sejong.elasticservice.project.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private DocumentDocument documentDocument;

    public static DocumentIndexEvent fromJson(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message, DocumentIndexEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패");
        }
    }
}
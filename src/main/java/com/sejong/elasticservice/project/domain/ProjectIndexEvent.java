package com.sejong.elasticservice.project.domain;


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
public class ProjectIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private ProjectEvent projectEvent;

    public static ProjectIndexEvent fromJson(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message, ProjectIndexEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패");
        }
    }
}
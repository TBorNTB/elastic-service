package com.sejong.elasticservice.domain.rag.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponseDto {
    private String id;
    private String content;
    private Map<String, Object> metadata;
    private Double score;
}

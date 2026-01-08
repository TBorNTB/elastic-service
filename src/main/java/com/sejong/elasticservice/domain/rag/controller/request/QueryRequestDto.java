package com.sejong.elasticservice.domain.rag.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryRequestDto {
    @NotBlank
    private String query;
    private Integer maxResults;
    private String model;
}

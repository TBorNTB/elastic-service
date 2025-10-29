package com.sejong.elasticservice.rag.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryResponseDto {
    private String query;
    private String answer;
    private List<DocumentResponseDto> relevantDocuments;

    public static QueryResponseDto of(String query, String answer, List<DocumentResponseDto> relevantDocuments) {
        return QueryResponseDto.builder()
                .query(query)
                .answer(answer)
                .relevantDocuments(relevantDocuments)
                .build();
    }
}

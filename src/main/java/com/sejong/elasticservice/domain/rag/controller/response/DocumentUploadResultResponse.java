package com.sejong.elasticservice.domain.rag.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentUploadResultResponse {
    private String documentId;
    private String message;

    public static DocumentUploadResultResponse of(String documentId, String message) {
        return DocumentUploadResultResponse.builder()
                .documentId(documentId)
                .message(message)
                .build();
    }
}

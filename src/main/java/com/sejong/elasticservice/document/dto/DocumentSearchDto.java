package com.sejong.elasticservice.document.dto;

import com.sejong.elasticservice.document.domain.DocumentDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchDto {
    private Long id;
    private String yorkieDocumentId;
    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;
    private String createdAt;
    private String updatedAt;

    public static DocumentSearchDto toDocumentSearchDto(DocumentDocument document) {
        return DocumentSearchDto.builder()
                .id(Long.parseLong(document.getId()))
                .yorkieDocumentId(document.getYorkieDocumentId())
                .title(document.getTitle())
                .content(document.getContent())
                .description(document.getDescription())
                .thumbnailUrl(document.getThumbnailUrl())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
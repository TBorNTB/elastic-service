package com.sejong.elasticservice.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentEvent {

    private String id;
    private String yorkieDocumentId;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private String createdAt;
    private String updatedAt;

}

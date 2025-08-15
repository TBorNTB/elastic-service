package com.sejong.elasticservice.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDocument {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String id;
    private String yorkieDocumentId;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private String createdAt;
    private String updatedAt;

}

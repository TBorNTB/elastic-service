package com.sejong.projectservice.infrastructure.document.kafka;


import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.project.kafka.ProjectDocument;
import com.sejong.projectservice.infrastructure.project.kafka.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.print.Doc;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private DocumentDocument documentDocument;

    public static DocumentIndexEvent of(Document document, Type type, long occurredAt) {
        DocumentDocument documentDocument = DocumentDocument.from(document);
        return DocumentIndexEvent.builder()
                .aggregatedId(documentDocument.getId())
                .type(type)
                .occurredAt(occurredAt)
                .documentDocument(documentDocument)
                .build();
    }

    public static DocumentIndexEvent deleteOf(String documentId, Type type, long occurredAt) {
        return DocumentIndexEvent.builder()
                .aggregatedId(documentId)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}
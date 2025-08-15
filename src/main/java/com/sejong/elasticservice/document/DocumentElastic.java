package com.sejong.projectservice.infrastructure.document.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sejong.projectservice.core.document.domain.DocumentDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "documents")
@Setting(settingPath = "/elasticsearch/document-settings.json")
public class DocumentElastic {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String yorkieDocumentId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "documents_title_analyzer" ),
            otherFields = {
                    @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
            }
    )
    private String title;

    @Field(type = FieldType.Text , analyzer = "documents_description_analyzer")
    private String description;

    @Field(type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(type = FieldType.Text , analyzer = "documents_content_analyzer")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String createdAt;

    public static DocumentElastic from(com.sejong.projectservice.core.document.domain.Document document){
        String fullTime = document.getCreatedAt().toString();
        String cut = fullTime.length() > 23 ? fullTime.substring(0, 23) : fullTime;

        return DocumentElastic.builder()
                .id(document.getId().toString())
                .yorkieDocumentId(document.getYorkieDocumentId())
                .title(document.getTitle())
                .description(document.getDescription())
                .thumbnailUrl(document.getThumbnailUrl())
                .content(document.getContent())
                .createdAt(cut)
                .build();
    }

    public DocumentDocument toDocument(){
        return DocumentDocument.builder()
                .id(Long.valueOf(id))
                .yorkieDocumentId(yorkieDocumentId)
                .title(title)
                .content(content)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .createdAt(createdAt)
                .build();
    }
}

package com.sejong.elasticservice.domain.csknowledge.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sejong.elasticservice.common.embedded.Names;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "cs-knowledge")
@Setting(settingPath = "/elasticsearch/cs-knowledge-settings.json")
public class CsKnowledgeDocument {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "cs-knowledge_title_analyzer"),
            otherFields = { @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori") }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "cs-knowledge_content_analyzer")
    private String content;

    @Field(type = FieldType.Text, analyzer =  "cs-knowledge_description_analyzer")
    private String description;

    @Field(type = FieldType.Object)
    private Names writer;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "cs-knowledge_category_analyzer"),
            otherFields = {@InnerField(suffix = "raw", type = FieldType.Keyword)}
    )
    private String category;

    @Field(type = FieldType.Keyword)
    private String thumbnailUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String createdAt;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long likeCount = 0L;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long viewCount = 0L;

    public static CsKnowledgeDocument from(CsKnowledgeEvent csKnowledgeEvent, Names writer) {
        return CsKnowledgeDocument.builder()
                .id(csKnowledgeEvent.getId())
                .writer(writer)
                .description(csKnowledgeEvent.getDescription())
                .title(csKnowledgeEvent.getTitle())
                .content(csKnowledgeEvent.getContent())
                .category(csKnowledgeEvent.getCategory())
                .thumbnailUrl(csKnowledgeEvent.getThumbnailUrl())
                .createdAt(csKnowledgeEvent.getCreatedAt())
                .likeCount(csKnowledgeEvent.getLikeCount())
                .viewCount(csKnowledgeEvent.getViewCount())
                .build();
    }
}
package com.sejong.elasticservice.csknowledge.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "csknowledge")
@Setting(settingPath = "/elasticsearch/csknowledge-settings.json")
public class CsKnowledgeDocument {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "csknowledge_title_analyzer"),
            otherFields = { @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori") }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "csknowledge_content_analyzer")
    private String content;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "csknowledge_category_analyzer"),
            otherFields = {@InnerField(suffix = "raw", type = FieldType.Keyword)}
    )
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String createdAt;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long likeCount = 0L;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long viewCount = 0L;

    public static CsKnowledgeDocument from(CsKnowledgeEvent csKnowledgeEvent) {
        return CsKnowledgeDocument.builder()
                .id(csKnowledgeEvent.getId())
                .title(csKnowledgeEvent.getTitle())
                .content(csKnowledgeEvent.getContent())
                .category(csKnowledgeEvent.getCategory())
                .createdAt(csKnowledgeEvent.getCreatedAt())
                .likeCount(csKnowledgeEvent.getLikeCount())
                .viewCount(csKnowledgeEvent.getViewCount())
                .build();
    }
}
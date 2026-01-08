package com.sejong.elasticservice.domain.news.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "news_title_analyzer"),
            otherFields = {
                    @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
            }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "news_summary_analyzer")
    private String summary;

    @Field(type = FieldType.Text, analyzer = "news_content_analyzer")
    private String content;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "news_category_analyzer"),
            otherFields = {@InnerField(suffix = "raw", type = FieldType.Keyword)}
    )
    private NewsCategory category;
}
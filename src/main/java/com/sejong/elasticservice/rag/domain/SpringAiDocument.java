package com.sejong.elasticservice.rag.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "spring-ai-document-index")
@Setting(settingPath = "/elasticsearch/spring-ai-document-settings.json")
public class SpringAiDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text
        , analyzer = "spring_ai_content_analyzer")
    private String content;

    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] vector;

    @Field(type = FieldType.Object, enabled = true)
    private Object metadata;
}
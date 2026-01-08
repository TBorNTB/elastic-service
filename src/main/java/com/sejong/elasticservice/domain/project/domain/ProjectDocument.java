package com.sejong.elasticservice.domain.project.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.common.embedded.Names;
import java.util.Map;
import java.util.Map.Entry;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName= "projects")
@Setting(settingPath = "/elasticsearch/project-settings.json")
public class ProjectDocument {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "projects_title_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori") }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "projects_description_analyzer")
    private String description;

    @Field(type = FieldType.Keyword)
    private ProjectStatus projectStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String updatedAt;

    @Field(type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(type = FieldType.Keyword)
    private List<String> projectCategories = new ArrayList<>();

    @Field(type = FieldType.Keyword)
    private List<String> projectTechStacks = new ArrayList<>();

    @Field(type = FieldType.Object)
    private Names owner;

    @Field(type = FieldType.Object)
    private List<Names> collaborators = new ArrayList<>();

    // ✅ 카운터 기본 0
    @Builder.Default
    @Field(type = FieldType.Long)
    private long likeCount = 0L;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long viewCount = 0L;

    public static ProjectDocument from(ProjectEvent pe, Names owner, List<Names> collaborators){
        return ProjectDocument.builder()
                .id(pe.getId())
                .title(pe.getTitle())
                .description(pe.getDescription())
                .thumbnailUrl(pe.getThumbnailUrl())
                .projectStatus(pe.getProjectStatus())
                .createdAt(pe.getCreatedAt())
                .updatedAt(pe.getUpdatedAt())
                .projectCategories(pe.getProjectCategories())
                .projectTechStacks(pe.getProjectTechStacks())
                .owner(owner)
                .collaborators(collaborators)
                .likeCount(pe.getLikeCount())
                .viewCount(pe.getViewCount())
                .build();
    }
}
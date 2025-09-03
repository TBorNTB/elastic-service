package com.sejong.elasticservice.project;


import com.fasterxml.jackson.annotation.JsonFormat;
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
            otherFields = { @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori") }
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

    @Field(type = FieldType.Keyword)
    private List<String> collaborators = new ArrayList<>();

    // ✅ 카운터 기본 0
    @Builder.Default
    @Field(type = FieldType.Long)
    private long likeCount = 0L;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long viewCount = 0L;

    public static ProjectDocument from(ProjectEvent project){
        return ProjectDocument.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .projectCategories(project.getProjectCategories())
                .projectTechStacks(project.getProjectTechStacks())
                .collaborators(project.getCollaborators())
                .likeCount(project.getLikeCount())
                .viewCount(project.getViewCount())
                .build();
    }

    public ProjectEvent toDocument(){
        return ProjectEvent.builder()
                .id(id)
                .title(title)
                .description(description)
                .projectStatus(projectStatus)
                .thumbnailUrl(thumbnailUrl)
                .createdAt(createdAt)
                .updatedAt(updatedAt)                 // (기존 createdAt -> updatedAt으로 수정 권장)
                .projectCategories(projectCategories)
                .projectTechStacks(projectTechStacks)
                .collaborators(collaborators)
                .likeCount(likeCount)
                .viewCount(viewCount)
                .build();
    }
}
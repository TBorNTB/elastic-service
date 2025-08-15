package com.sejong.projectservice.infrastructure.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.kafka.ProjectDocument;
import com.sejong.projectservice.core.techstack.TechStack;
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
public class ProjectElastic {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "projects_title_analyzer" ),
            otherFields = {
                    @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
            }
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

    public static ProjectElastic from(Project project){

        List<String> categoryNames = project.getCategories().stream()
                .map(Category::getName)
                .distinct()
                .toList();

        List<String> techStackNames = project.getTechStacks().stream()
                .map(TechStack::getName)
                .distinct()
                .toList();

        List<String> collaboratorNames = project.getCollaborators().stream()
                .map(Collaborator::getCollaboratorName)
                .distinct()
                .toList();

        return ProjectElastic.builder()
                .id(project.getId().toString())
                .title(project.getTitle())
                .description(project.getDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt().toString())
                .updatedAt(project.getUpdatedAt().toString())
                .projectCategories(categoryNames)
                .projectTechStacks(techStackNames)
                .collaborators(collaboratorNames)
                .build();
    }

    public ProjectDocument toDocument(){
        return ProjectDocument.builder()
                .id(id)
                .title(title)
                .description(description)
                .projectStatus(projectStatus)
                .thumbnailUrl(thumbnailUrl)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .projectCategories(projectCategories)
                .projectTechStacks(projectTechStacks)
                .collaborators(collaborators)
                .build();
    }
}

package com.sejong.elasticservice.domain.news.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sejong.elasticservice.common.embedded.Names;
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
@Document(indexName = "news")
@Setting(settingPath = "/elasticsearch/news-settings.json")
public class NewsDocument {

    @Id
    private String id;

    @Field(type = FieldType.Object)
    private Content content;

    @Field(type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(type = FieldType.Object)
    private Names writer;

    @Field(type = FieldType.Object)
    private List<Names> participants = new ArrayList<>();

    @Field(type = FieldType.Keyword)
    private List<String> tags = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private String updatedAt;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long likeCount = 0L;

    @Builder.Default
    @Field(type = FieldType.Long)
    private long viewCount = 0L;

    public static NewsDocument from(NewsEvent newsEvent, Names writer, List<Names> participants) {
        return NewsDocument.builder()
                .id(newsEvent.getId())
                .content(newsEvent.getContent())
                .thumbnailUrl(newsEvent.getThumbnailUrl())
                .writer(writer)
                .participants(participants)
                .tags(newsEvent.getTags())
                .createdAt(newsEvent.getCreatedAt())
                .updatedAt(newsEvent.getUpdatedAt())
                .likeCount(newsEvent.getLikeCount())
                .viewCount(newsEvent.getViewCount())
                .build();
    }
}
package com.sejong.elasticservice.common.embedded;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Names {
    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Keyword)
    private String nickname;

    @Field(type = FieldType.Keyword)
    private String realname;
}

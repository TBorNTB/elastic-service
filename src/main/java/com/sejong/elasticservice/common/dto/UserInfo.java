package com.sejong.elasticservice.common.dto;

import com.sejong.elasticservice.common.embedded.Names;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String nickname;
    private String realname;
    private String profileImageUrl;

    public static UserInfo from(Names names) {
        if (names == null) {
            return null;
        }
        return UserInfo.builder()
                .username(names.getUsername())
                .nickname(names.getNickname())
                .realname(names.getRealname())
                .profileImageUrl(names.getProfileImageUrl())
                .build();
    }
}

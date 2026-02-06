package com.sejong.elasticservice.domain;

import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.embedded.Names;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNameInfoService {
    private final UserExternalService userExternalService;

    public Map<String, UserNameInfo> getUserNameInfos(List<String> usernames) {
        return userExternalService.getUserNameInfos(usernames);
    }

    public Names toNames(String username, Map<String, UserNameInfo> infos) {
        UserNameInfo info = infos.get(username);
        if (info == null) {
            log.warn("User not found: {}", username);
            return new Names(username, null, null,null);
        }
        return new Names(username, info.nickname(), info.realName(), info.profileImageUrl());
    }
}

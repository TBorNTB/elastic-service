package com.sejong.elasticservice.client.service;

import static com.sejong.elasticservice.common.exception.ExceptionType.EXTERNAL_SERVICE_ERROR;

import com.sejong.elasticservice.client.UserClient;
import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.common.exception.BaseException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserExternalService {

    private final UserClient userClient;

    @CircuitBreaker()
    public Map<String, UserNameInfo> getUserNameInfos(List<String> usernames) {
        ResponseEntity<Map<String, UserNameInfo>> response = userClient.getUserNameInfos(usernames);
        if (response.getBody() == null) {
            throw new BaseException(EXTERNAL_SERVICE_ERROR);
        }
        return response.getBody();
    }

    private Map<String, UserNameInfo> getUserNameInfosFallback(List<String> usernames, Throwable t) {
        log.info("getUserNameInfosFallback 작동");
        if (t instanceof BaseException) {
            throw (BaseException) t;
        }

        throw new BaseException(EXTERNAL_SERVICE_ERROR);
    }
}

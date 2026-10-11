package com.sejong.elasticservice.client.service;

import static com.sejong.elasticservice.common.exception.ExceptionType.EXTERNAL_SERVICE_ERROR;

import com.sejong.elasticservice.client.UserClient;
import com.sejong.elasticservice.client.response.CommentCountResponse;
import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.common.exception.BaseException;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public Map<Long, Long> getCommentCounts(PostType postType, List<Long> postIds) {
        try {
            ResponseEntity<List<CommentCountResponse>> response = userClient.getCommentCounts(postType, postIds);
            if (response.getBody() == null) {
                throw new BaseException(EXTERNAL_SERVICE_ERROR);
            }

            return response.getBody().stream()
                    .collect(Collectors.toMap(CommentCountResponse::postId, CommentCountResponse::count));
        } catch (RuntimeException e) {
            log.warn("댓글 수 조회에 실패했습니다. 댓글 수를 0으로 표시합니다.", e);
            return Map.of();
        }
    }
}

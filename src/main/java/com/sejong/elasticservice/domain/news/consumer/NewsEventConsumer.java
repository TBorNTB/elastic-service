package com.sejong.elasticservice.domain.news.consumer;

import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.common.constants.Type;
import com.sejong.elasticservice.common.embedded.Names;
import com.sejong.elasticservice.domain.UserNameInfoService;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.news.domain.NewsEvent;
import com.sejong.elasticservice.domain.news.domain.NewsEventMeta;
import com.sejong.elasticservice.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsEventConsumer {

    private final NewsRepository repo;
    private final UserNameInfoService userNameInfoService;

    @KafkaListener(
            topics = TopicNames.NEWS,
            groupId = GroupNames.NEWS
    )
    public void consume(String message) {
        NewsEventMeta meta = NewsEventMeta.fromJson(message);
        log.info("뉴스 이벤트 consume");

        if (meta.getType() == Type.CREATED || meta.getType() == Type.UPDATED) {
            Result result = getResult(meta);
            NewsDocument newsDocument = NewsDocument.from(
                    meta.getNewsEvent(), result.writer(), result.participants());
            repo.save(newsDocument);
        } else if (meta.getType() == Type.DELETED) {
            repo.deleteById(meta.getAggregatedId());
        }
    }

    private Result getResult(NewsEventMeta meta) {
        NewsEvent newsEvent = meta.getNewsEvent();
        String writerUsername = newsEvent.getWriterId();
        List<String> participantIds = newsEvent.getParticipantIds();

        List<String> usernames = new ArrayList<>();
        usernames.add(writerUsername);
        if (participantIds != null) {
            usernames.addAll(participantIds);
        }

        Map<String, UserNameInfo> infos = userNameInfoService.getUserNameInfos(usernames);

        Names writer = userNameInfoService.toNames(writerUsername, infos);

        List<Names> participants = participantIds == null
                ? new ArrayList<>()
                : participantIds.stream()
                        .map(id -> userNameInfoService.toNames(id, infos))
                        .toList();

        return new Result(writer, participants);
    }
    private record Result(Names writer, List<Names> participants) {
    }
}
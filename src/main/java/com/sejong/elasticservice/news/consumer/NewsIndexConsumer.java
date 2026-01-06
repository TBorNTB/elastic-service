package com.sejong.elasticservice.news.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.common.constants.Type;
import com.sejong.elasticservice.news.domain.NewsIndexEvent;
import com.sejong.elasticservice.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsIndexConsumer {
    private final NewsRepository repo;

    @KafkaListener(
            topics = TopicNames.NEWS,
            groupId = GroupNames.NEWS
    )
    public void consume(String message) {
        NewsIndexEvent event = NewsIndexEvent.fromJson(message);
        log.info("뉴스 이벤트 consume");
        if (event.getType() == Type.CREATED || event.getType() == Type.UPDATED) {
            repo.save(event.getNewsEvent());
        }
        if (event.getType() == Type.DELETED) {
            repo.deleteById(event.getAggregatedId());
        }
    }
}
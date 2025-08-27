package com.sejong.elasticservice.postlike;

import com.sejong.elasticservice.TopicNames;
import com.sejong.elasticservice.project.ProjectElasticRepository;
import com.sejong.elasticservice.project.ProjectIndexEvent;
import com.sejong.elasticservice.project.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostlikeConsumer {
    private final ProjectElasticRepository projectElasticRepository;

    @KafkaListener(
            topics = TopicNames.POSTLIKE_EVENTS,
            groupId = "like-group"
    )
    public void consume(String message) {

        PostLikeEvent event = PostLikeEvent.fromJson(message);

        if(event.getPostType()==PostType.PROJECT){
            projectElasticRepository.update(event.getPostId(), event.getLikeCount());
        }
        else if (event.getPostType()==PostType.ARTICLE){

        }
        else if (event.getPostType()==PostType.NEWS){

        }
    }
}

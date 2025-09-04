package com.sejong.elasticservice.postlike;

import com.sejong.elasticservice.common.name.GroupNames;
import com.sejong.elasticservice.common.name.TopicNames;
import com.sejong.elasticservice.project.ProjectElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostlikeConsumer {
    private final ProjectElasticRepository projectElasticRepository;

    @KafkaListener(
            topics = TopicNames.POSTLIKE,
            groupId = GroupNames.LIKE
    )
    public void consume(String message) {

        PostLikeEvent event = PostLikeEvent.fromJson(message);

        if(event.getPostType()==PostType.PROJECT){
            projectElasticRepository.updateLikeCount(event.getPostId(), event.getLikeCount());
        }
        else if (event.getPostType()==PostType.ARTICLE){

        }
        else if (event.getPostType()==PostType.NEWS){

        }
    }
}

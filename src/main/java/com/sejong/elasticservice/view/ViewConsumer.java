package com.sejong.elasticservice.view;

import com.sejong.elasticservice.TopicNames;
import com.sejong.elasticservice.postlike.PostLikeEvent;
import com.sejong.elasticservice.postlike.PostType;
import com.sejong.elasticservice.project.ProjectElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewConsumer {
    private final ProjectElasticRepository projectElasticRepository;

    @KafkaListener(
            topics = TopicNames.VIEW_EVENTS,
            groupId = "view-group"
    )
    public void consume(String message) {

        ViewEvent event = ViewEvent.fromJson(message);

        if(event.getPostType()== PostType.PROJECT){
            projectElasticRepository.updateViewCount(event.getPostId(), event.getViewCount());
        }
        else if (event.getPostType()==PostType.ARTICLE){

        }
        else if (event.getPostType()==PostType.NEWS){

        }
    }
}

package com.sejong.elasticservice.view.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.postlike.PostType;
import com.sejong.elasticservice.project.repository.ProjectElasticRepository;
import com.sejong.elasticservice.view.domain.ViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewConsumer {
    private final ProjectElasticRepository projectElasticRepository;

    @KafkaListener(
            topics = TopicNames.VIEW,
            groupId = GroupNames.VIEW
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

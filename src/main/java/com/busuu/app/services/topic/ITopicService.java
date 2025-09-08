package com.busuu.app.services.topic;

import com.busuu.app.dtos.responses.TopicResponse;
import com.busuu.app.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ITopicService
{
    List<TopicResponse> getTopicsByType(String requestId, String topicType, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String videoCategory) throws DataNotFoundException;

    TopicResponse getTopic(String requestId, String topicId);

}

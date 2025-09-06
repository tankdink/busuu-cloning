package com.busuu.app.services.topicCategory;

import com.busuu.app.entities.topics.TopicCategory;

import java.util.List;

public interface ITopicCategoryService
{
    List<TopicCategory> getTopicCategories(String requestId, String topicType);

}

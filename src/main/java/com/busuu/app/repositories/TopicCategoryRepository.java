package com.busuu.app.repositories;

import com.busuu.app.entities.topics.TopicCategory;
import com.busuu.app.entities.enums.TopicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicCategoryRepository extends JpaRepository<TopicCategory, String>
{

    List<TopicCategory> findByTopicType(TopicType topicType);

}

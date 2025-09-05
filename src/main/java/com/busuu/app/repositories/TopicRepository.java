package com.busuu.app.repositories;

import com.busuu.app.entities.topics.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String>, JpaSpecificationExecutor<Topic>
{


}

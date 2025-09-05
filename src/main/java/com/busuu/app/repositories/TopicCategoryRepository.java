package com.busuu.app.repositories;

import com.busuu.app.entities.topics.TopicCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicCategoryRepository extends JpaRepository<TopicCategory, String>
{

}

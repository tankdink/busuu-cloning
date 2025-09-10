package com.busuu.app.repositories;

import com.busuu.app.entities.posts.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String>, JpaSpecificationExecutor<Post>
{
    List<Post> findByUserId(String userId, Sort sort);


}

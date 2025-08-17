package com.busuu.app.specification;

import com.busuu.app.entities.post.Post;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PostSpecification
{

    public static Specification<Post> getSpecification(
            List<String> filterBy,
            List<String> filterValue,
            List<String> sortBy,
            List<String> sortDirection
    ) {

        return null;

    }
}

package com.busuu.app.specification;

import com.busuu.app.entities.Language;
import com.busuu.app.entities.User;
import com.busuu.app.entities.posts.Post;
import com.busuu.app.entities.posts.PostType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PostSpecification
{

    private static final Set<String> SORT_FIELDS = Set.of("createdAt");


    public static Specification<Post> getSpecification(
            String postType,
            String language,
            List<String> sortBy,
            List<String> sortDirection,
            String userId
    ) {

        return (Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            //Predicate act like a single condition
            List<Predicate> predicates = new ArrayList<>();

            //Join
            Join<Post, Language> languageJoin = root.join("language", JoinType.LEFT);
            Join<Post, User> userJoin = root.join("user", JoinType.LEFT);


            //Filter
            //Filter by user first
//            if (userId != null && !userId.isEmpty())
//            {
//                if (postType != null && !postType.isEmpty())
//                {
//                    predicates.add(cb.and(
//                            cb.equal(root.get("postType"), PostType.valueOf(postType.toUpperCase())),
//                            cb.equal(userJoin.get("id"), userId)
//                    ));
//                }
//                if (language != null && !language.isEmpty())
//                {
//                    predicates.add(cb.and(
//                            cb.equal(cb.lower(languageJoin.get("name")), language.toLowerCase()),
//                            cb.equal(userJoin.get("id"), userId)
//                    ));
//                }
//
//            }
//            else
//            {
//                if (postType != null && !postType.isEmpty()) {
//                    predicates.add(cb.equal(root.get("postType"), PostType.valueOf(postType.toUpperCase())));
//                }
//                if (language != null && !language.isEmpty()) {
//                    predicates.add(cb.equal(cb.lower(languageJoin.get("name")), language.toLowerCase()));
//                }
//            }

            //Filter
            if (userId != null && !userId.isEmpty()) {
                predicates.add(cb.equal(userJoin.get("id"), userId));
            }
            if (postType != null && !postType.isEmpty()) {
                predicates.add(cb.equal(root.get("postType"), PostType.valueOf(postType.toUpperCase())));
            }
            if (language != null && !language.isEmpty()) {
                predicates.add(cb.equal(cb.lower(languageJoin.get("name")), language.toLowerCase()));
            }

            //Sorting
            List<Order> orders = new ArrayList<>();

            if (sortBy != null && !sortBy.isEmpty())
            {
                for (int i = 0; i < sortBy.size(); i++)
                {
                    String sortColumn = sortBy.get(i);

                    String direction = "asc";
                    if ( sortDirection != null && i < sortDirection.size()) direction = sortDirection.get(i);

                    if (!direction.equals("asc") && !direction.equals("desc")) {
                        throw new IllegalArgumentException("Unsupported sort direction: " + direction + "; Support sort by: asc, desc");
                    }

                    if (!SORT_FIELDS.contains(sortColumn)) {
                        throw new IllegalArgumentException("Unsupported sort column: " + sortColumn + "; Support filter by: " + SORT_FIELDS);
                    }

                    switch (sortColumn)
                    {

                        case "createdAt":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("createdAt"))
                                    : cb.desc(root.get("createdAt")));
                            break;

                    }
                }
            }
            else //Default sort
            {
                orders.add(cb.desc(root.get("createdAt")));
            }

            //Avoid duplicate case
            orders.add(cb.asc(root.get("id")));

            query.orderBy(orders);

            //Criteria Builder (cb here) acting like a WHERE clause, which require predicate parameter is an Array of Predicate
            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }
}

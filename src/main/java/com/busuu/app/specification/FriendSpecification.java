package com.busuu.app.specification;

import com.busuu.app.entities.Friend;
import com.busuu.app.entities.User;
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

public class FriendSpecification
{

    private static final Set<String> SORT_FIELDS = Set.of("createdAt", "firstName");

    public static Specification<Friend> getSpecification(
            String userId,
            String searchValue,
            List<String> sortBy,
            List<String> sortDirection,
            String country
    ) {

        return (Root<Friend> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            //Predicate act like a single condition
            List<Predicate> predicates = new ArrayList<>();

            //Join
            Join<Friend, User> friendJoin = root.join("friend", JoinType.LEFT);


            //Get friend list first
            if (userId != null && !userId.isEmpty()) {
                predicates.add(cb.equal(friendJoin.get("id"), userId));
            }

            //Filter then search then sort

            //Filter
            if (country != null && !country.isEmpty()) {
                predicates.add(cb.equal(cb.lower(friendJoin.get("country")), country.toLowerCase()));
            }


            //Field search (LIKE SEARCH)
            if (searchValue != null && !searchValue.isEmpty())
            {

                Predicate fullNamePredicate = cb.like(cb.lower(friendJoin.get("fullName")), searchValue);

                predicates.add(fullNamePredicate);

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
                        case "firstName":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(friendJoin.get("firstName"))
                                    : cb.desc(friendJoin.get("firstName")));
                            break;

                    }
                }
            }
            else //Default sort
            {
                orders.add(cb.desc(friendJoin.get("firstName")));
            }

            //Avoid duplicate case
            orders.add(cb.asc(root.get("id")));

            query.orderBy(orders);

            //Criteria Builder (cb here) acting like a WHERE clause, which require predicate parameter is an Array of Predicate
            return cb.and(predicates.toArray(new Predicate[0]));

        };

    }

}

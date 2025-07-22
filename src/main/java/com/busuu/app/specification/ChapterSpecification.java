package com.busuu.app.specification;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.Level;
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

public class ChapterSpecification
{
    private static final Set<String> FILTER_FIELDS = Set.of("courseId", "levelId");
    private static final Set<String> SORT_FIELDS = Set.of("id", "title", "chapterOrder" , "courseTitle" , "levelCode");

    public static Specification<Chapter> getSpecification(
            String searchValue,
            List<String> filterBy,
            List<String> filterValue,
            List<String> sortBy,
            List<String> sortDirection
    )
    {
        return (Root<Chapter> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            //Predicate act like a single condition
            List<Predicate> predicates = new ArrayList<>();

            //Joining
            Join<Chapter, Course> courseJoin = root.join("course", JoinType.LEFT);
            Join<Chapter, Level> levelJoin = root.join("level", JoinType.LEFT);


            //Filter then search then sort

            //Filter
            if (filterBy != null && filterValue != null)
            {
                if (filterBy.size() != filterValue.size()) throw new IllegalArgumentException("filterBy and filterValue must have the same number of elements");

                for (int i = 0; i < filterBy.size(); i++)
                {
                    String column = filterBy.get(i);
                    String value = filterValue.get(i);

                    if (!FILTER_FIELDS.contains(column)) {
                        throw new IllegalArgumentException("Unsupported filter column: " + column + "; Support filter by: " + FILTER_FIELDS);
                    }

                    switch (column)
                    {
                        case "courseId":
                            predicates.add(cb.equal(cb.lower(courseJoin.get("id")), value.toLowerCase()));
                            break;
                        case "levelId":
                            predicates.add(cb.equal(levelJoin.get("id"), value.toLowerCase()));
                            break;
                    }
                }
            }



            //Global search (LIKE SEARCH)
            if (searchValue != null && !searchValue.isEmpty())
            {
                //For search not exact (cb.like)
                String val = "%" + searchValue.toLowerCase() + "%";

                //For search exact (cb.equal)
                //String val = searchValue.toLowerCase();

                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), val);
                Predicate chapterOrderPredicate = cb.like(cb.toString(root.get("chapterOrder")), val);
                Predicate courseIdPredicate = cb.like(cb.lower(courseJoin.get("title")), val);
                Predicate levelIdPredicate = cb.like(cb.lower(levelJoin.get("code")), val);


                predicates.add(cb.or(
                        titlePredicate,
                        chapterOrderPredicate,
                        courseIdPredicate,
                        levelIdPredicate));
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
                        case "id":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("id"))
                                    : cb.desc(root.get("id")));
                            break;
                        case "title":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("title"))
                                    : cb.desc(root.get("title")));
                            break;
                        case "chapterOrder":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(cb.toString(root.get("chapterOrder")))
                                    : cb.desc(cb.toString(root.get("chapterOrder"))));
                            break;
                        case "courseTitle":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(courseJoin.get("title"))
                                    : cb.desc(courseJoin.get("title")));
                            break;
                        case "levelCode":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(levelJoin.get("code"))
                                    : cb.desc(levelJoin.get("code")));
                            break;

                    }
                }
            }
            else //Default sort
            {

                orders.add(cb.asc(courseJoin.get("title")));
                orders.add(cb.asc(levelJoin.get("code")));
                orders.add(cb.asc(root.get("chapterOrder")));


            }

            //Avoid duplicate case
            orders.add(cb.asc(root.get("id")));


            query.orderBy(orders);

            //Criteria Builder (cb here) acting like a WHERE clause, which require predicate parameter is an Array of Predicate
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

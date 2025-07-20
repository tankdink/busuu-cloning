package com.busuu.app.specification;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.Level;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChapterSpecification
{
    private static final Set<String> FILTER_FIELDS = Set.of("courseId", "levelId");

    public static Specification<Chapter> getSpecification(
            String searchValue,
            List<String> filterBy,
            List<String> filterValue
    )
    {
        return (Root<Chapter> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            //Predicate act like a single condition
            List<Predicate> predicates = new ArrayList<>();


            //Filter then search

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
                            Join<Chapter, Course> courseJoin = root.join("course", JoinType.LEFT);
                            predicates.add(cb.equal(cb.lower(courseJoin.get("id")), value.toLowerCase()));
                            break;
                        case "levelId":
                            Join<Chapter, Level> levelJoin = root.join("level", JoinType.LEFT);
                            predicates.add(cb.equal(levelJoin.get("id"), value.toLowerCase()));
                            break;
                    }
                }
            }

            //Global search (EXACT SEARCH)
            if (searchValue != null && !searchValue.isEmpty())
            {
                //For seach not exact (cb.like)
                //String val = "%" + searchValue.toLowerCase() + "%";

                //For search exact (cb.equal)
                String val = searchValue.toLowerCase();

                Predicate idPredicate = cb.equal(cb.lower(root.get("id")), val);
                Predicate titlePredicate = cb.equal(cb.lower(root.get("title")), val);
                Predicate chapterOrderPredicate = cb.equal(cb.toString(root.get("chapterOrder")), val);

                Join<Chapter, Course> courseJoin = root.join("course", JoinType.LEFT);
                Predicate courseIdPredicate = cb.equal(cb.lower(courseJoin.get("id")), val);

                Join<Chapter, Level> levelJoin = root.join("level", JoinType.LEFT);
                Predicate levelIdPredicate = cb.equal(cb.lower(levelJoin.get("id")), val);


                predicates.add(cb.or(
                        idPredicate,
                        titlePredicate,
                        chapterOrderPredicate,
                        courseIdPredicate,
                        levelIdPredicate));
            }

            //Criteria Builder (cb here) acting like a WHERE clause, which require predicate parameter is an Array of Predicate
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

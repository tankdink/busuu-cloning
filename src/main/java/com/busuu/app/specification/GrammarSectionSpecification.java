package com.busuu.app.specification;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.Level;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarSectionSpecification
{
    private static final Set<String> FILTER_FIELDS = Set.of("grammarId", "lessonId", "levelId");
    private static final Set<String> SORT_FIELDS = Set.of("title", "description" , "content", "grammarSectionOrder", "grammarTitle", "lessonTitle", "levelCode" , "createdAt", "updatedAt");

    public static Specification<GrammarSection> getSpecification(
            String searchValue,
            List<String> filterBy,
            List<String> filterValue,
            List<String> sortBy,
            List<String> sortDirection
    )
    {
        return (Root<GrammarSection> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            //Predicate act like a single condition
            List<Predicate> predicates = new ArrayList<>();

            //Joining
            Join<GrammarSection, Grammar> grammarJoin = root.join("grammar", JoinType.LEFT);
            Join<GrammarSection, Lesson> lessonJoin = root.join("lesson", JoinType.LEFT);
            Join<GrammarSection, Level> levelJoin = root.join("level", JoinType.LEFT);

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
                        case "grammarId":
                            predicates.add(cb.equal(cb.lower(grammarJoin.get("id")), value.toLowerCase()));
                            break;
                        case "lessonId":
                            predicates.add(cb.equal(cb.lower(lessonJoin.get("id")), value.toLowerCase()));
                            break;
                        case "levelId":
                            predicates.add(cb.equal(cb.lower(levelJoin.get("id")), value.toLowerCase()));
                            break;

                    }
                }
            }



            //Global search (LIKE SEARCH)
            if (searchValue != null && !searchValue.isEmpty())
            {

                String val = null;
                String[] range = null;
                Boolean isDateInput = false;

                //Process for date-time and date input

                String dateTimeRegex = "^([01]?[0-9]|2[0-3]):([0-5]?[0-9])\\s([0-2]?[0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$";
                String dateRegex = "^([0-2]?[0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$";


                // Create a pattern and matcher
                Pattern dateTimePattern = Pattern.compile(dateTimeRegex);
                Matcher dateTimeMatcher = dateTimePattern.matcher(searchValue);

                Pattern datePattern = Pattern.compile(dateRegex);
                Matcher dateMatcher = datePattern.matcher(searchValue);

                if (dateTimeMatcher.matches()) val = formatDateTime(searchValue);
                else if (dateMatcher.matches()) isDateInput = true;
                else val = "%" + searchValue.toLowerCase() + "%"; //For search not exact (cb.like)

                //For search exact (cb.equal)
                //String val = searchValue.toLowerCase();

                Predicate createdAtPredicate = null;
                Predicate updatedAtPredicate = null;

                if (isDateInput)
                {
                    LocalDateTime[] dateRange = formatDateToRange(searchValue);
                    createdAtPredicate = cb.between(root.get("createdAt"), dateRange[0], dateRange[1]);
                    updatedAtPredicate = cb.between(root.get("updatedAt"), dateRange[0], dateRange[1]);

                }
                else
                {
                    createdAtPredicate = cb.like(
                            cb.lower(cb.function("DATE_FORMAT", String.class, root.get("createdAt"), cb.literal("%H:%i %d/%m/%Y"))),
                            val
                    );
                    updatedAtPredicate = cb.like(
                            cb.lower(cb.function("DATE_FORMAT", String.class, root.get("updatedAt"), cb.literal("%H:%i %d/%m/%Y"))),
                            val
                    );
                }


                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), val);
                Predicate descriptionPredicate = cb.like(cb.toString(root.get("description")), val);
                Predicate contentPredicate = cb.like(cb.toString(root.get("content")), val);
                Predicate grammarSectionOrderPredicate = cb.like(cb.toString(root.get("grammarSectionOrder")), val);
                Predicate grammarTitlePredicate = cb.like(cb.lower(grammarJoin.get("title")), val);
                Predicate lessonTitlePredicate = cb.like(cb.lower(lessonJoin.get("title")), val);
                Predicate levelCodePredicate = cb.like(cb.lower(levelJoin.get("code")), val);


                predicates.add(cb.or(
                        createdAtPredicate,
                        updatedAtPredicate,
                        titlePredicate,
                        descriptionPredicate,
                        contentPredicate,
                        grammarSectionOrderPredicate,
                        grammarTitlePredicate,
                        lessonTitlePredicate,
                        levelCodePredicate));
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

                        case "title":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("title"))
                                    : cb.desc(root.get("title")));
                            break;
                        case "description":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("description"))
                                    : cb.desc(root.get("description")));
                            break;
                        case "content":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("content"))
                                    : cb.desc(root.get("content")));
                            break;
                        case "grammarSectionOrder":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(cb.toString(root.get("grammarSectionOrder")))
                                    : cb.desc(cb.toString(root.get("grammarSectionOrder"))));
                            break;
                        case "grammarTitle":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(cb.toString(grammarJoin.get("title")))
                                    : cb.desc(cb.toString(grammarJoin.get("title"))));
                            break;
                        case "lessonTitle":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(cb.toString(lessonJoin.get("title")))
                                    : cb.desc(cb.toString(lessonJoin.get("title"))));
                            break;
                        case "levelCode":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(cb.toString(levelJoin.get("code")))
                                    : cb.desc(cb.toString(levelJoin.get("code"))));
                            break;
                        case "createdAt":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("createdAt"))
                                    : cb.desc(root.get("createdAt")));
                            break;
                        case "updatedAt":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("updatedAt"))
                                    : cb.desc(root.get("updatedAt")));
                            break;

                    }
                }
            }
            else //Default sort
            {

                orders.add(cb.asc(grammarJoin.get("title")));
                orders.add(cb.asc(levelJoin.get("code")));
                orders.add(cb.asc(root.get("grammarSectionOrder")));
                orders.add(cb.asc(lessonJoin.get("title")));

            }

            //Avoid duplicate case
            orders.add(cb.asc(root.get("id")));


            query.orderBy(orders);

            //Criteria Builder (cb here) acting like a WHERE clause, which require predicate parameter is an Array of Predicate
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String formatDateTime(String userInput)
    {
        //The input format the user gives
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        //Parse the user input
        LocalDateTime dateTime = LocalDateTime.parse(userInput, inputFormatter);

        //UTC +7
        dateTime = dateTime.minusHours(7);

        //Format the adjusted date/time back to string
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        // Return the formatted adjusted string
        return dateTime.format(outputFormatter);
    }

    public static LocalDateTime[] formatDateToRange(String userInput)
    {
        //Will get chapter which between 17h the previous day of input to 17h of the day of input
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(userInput, inputFormatter);

        LocalDateTime start = date.minusDays(1).atTime(17, 0);
        LocalDateTime end = date.atTime(17, 0);

        return new LocalDateTime[]{start, end};
    }
}

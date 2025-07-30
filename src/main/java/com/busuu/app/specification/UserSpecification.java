package com.busuu.app.specification;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.Level;
import com.busuu.app.entities.Role;
import com.busuu.app.entities.User;
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

public class UserSpecification
{
    private static final Set<String> FILTER_FIELDS = Set.of("isActive");
    private static final Set<String> SORT_FIELDS = Set.of("firstName", "lastName", "fullName", "email", "phoneNumber", "createdAt", "updatedAt", "lastLogin", "isActive");

    public static Specification<User> getSpecification(
            String roleName,
            String searchValue,
            List<String> filterBy,
            List<String> filterValue,
            List<String> sortBy,
            List<String> sortDirection
    )
    {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            //Predicate act like a single condition
            List<Predicate> predicates = new ArrayList<>();

            //Joining
            Join<User, Role> roleJoin = root.join("roles", JoinType.LEFT);

            //Get by role name
            predicates.add(cb.equal(cb.lower(roleJoin.get("name")), roleName.toLowerCase()));


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
                        case "isActive":
                            boolean boolValue = Boolean.parseBoolean(value);
                            predicates.add(cb.equal(root.get("isActive"), boolValue));
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
                Predicate lastLoginPredicate = null;

                if (isDateInput)
                {
                    LocalDateTime[] dateRange = formatDateToRange(searchValue);
                    createdAtPredicate = cb.between(root.get("createdAt"), dateRange[0], dateRange[1]);
                    updatedAtPredicate = cb.between(root.get("updatedAt"), dateRange[0], dateRange[1]);
                    lastLoginPredicate = cb.between(root.get("lastLogin"), dateRange[0], dateRange[1]);

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
                    lastLoginPredicate = cb.like(
                            cb.lower(cb.function("DATE_FORMAT", String.class, root.get("lastLogin"), cb.literal("%H:%i %d/%m/%Y"))),
                            val
                    );
                }

                Predicate firstNamePredicate = cb.like(cb.lower(root.get("firstName")), val);
                Predicate lastNamePredicate = cb.like(cb.toString(root.get("lastName")), val);
                Predicate fullNamePredicate = cb.like(cb.lower(root.get("fullName")), val);
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), val);
                Predicate phoneNumberPredicate = cb.like(cb.lower(root.get("phoneNumber")), val);

                String active = val.replaceAll("^%|%$","");
                boolean boolValue = Boolean.parseBoolean(active);
                Predicate isActivePredicate = cb.equal(root.get("isActive"), boolValue);

                predicates.add(cb.or(
                        createdAtPredicate,
                        updatedAtPredicate,
                        lastLoginPredicate,
                        firstNamePredicate,
                        lastNamePredicate,
                        fullNamePredicate,
                        emailPredicate,
                        phoneNumberPredicate,
                        isActivePredicate));
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

                        case "firstName":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("firstName"))
                                    : cb.desc(root.get("firstName")));
                            break;
                        case "lastName":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(cb.toString(root.get("lastName")))
                                    : cb.desc(cb.toString(root.get("lastName"))));
                            break;
                        case "fullName":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("fullName"))
                                    : cb.desc(root.get("fullName")));
                            break;
                        case "email":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("email"))
                                    : cb.desc(root.get("email")));
                            break;
                        case "phoneNumber":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("phoneNumber"))
                                    : cb.desc(root.get("phoneNumber")));
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
                        case "lastLogin":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("lastLogin"))
                                    : cb.desc(root.get("lastLogin")));
                            break;
                        case "isEnabled":
                            orders.add(direction.equalsIgnoreCase("asc")
                                    ? cb.asc(root.get("isActive"))
                                    : cb.desc(root.get("isActive")));
                            break;

                    }
                }
            }
            else //Default sort
            {
                orders.add(cb.desc(root.get("isActive")));
                orders.add(cb.asc(root.get("firstName")));
                orders.add(cb.asc(root.get("lastName")));

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

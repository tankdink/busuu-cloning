package com.busuu.app.specification;

import com.busuu.app.entities.Correction;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CorrectionSpecification
{
    public static Specification<Correction> getSpecification(
            List<String> filterBy,
            List<String> filterValue,
            List<String> sortBy,
            List<String> sortDirection
    ) {

        return null;

    }
}

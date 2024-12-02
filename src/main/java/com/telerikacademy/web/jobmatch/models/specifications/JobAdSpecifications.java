package com.telerikacademy.web.jobmatch.models.specifications;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobAdSpecifications {

    public static Specification<JobAd> filterByOptions(
            JobAdFilterOptions jobAdFilterOptions
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(); // Start with an empty list of predicates

            jobAdFilterOptions.getPositionTitle().ifPresent(title ->
                    predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("positionTitle")), "%" + title.toLowerCase() + "%"
                            )
                    )
            );

            jobAdFilterOptions.getMinSalary().ifPresent(minSalary ->
                    predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("maxSalary"), minSalary)
                    )
            );

            jobAdFilterOptions.getMaxSalary().ifPresent(maxSalary ->
                    predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(root.get("minSalary"), maxSalary)
                    )
            );

            jobAdFilterOptions.getCreator().ifPresent(companyName ->
                    predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.join("employer").get("companyName")), "%" + companyName.toLowerCase() + "%"
                            )
                    )
            );

            jobAdFilterOptions.getLocation().ifPresent(location -> {
                        Join<JobAd, Location> locationJoin = root.join("location");
                        Predicate locationPredicate = criteriaBuilder.equal(locationJoin.get("name"), location);
                        Predicate homePredicate = criteriaBuilder.equal(locationJoin.get("name"), "Home");
                        predicates.add(criteriaBuilder.or(locationPredicate, homePredicate));
                    }

            );

            jobAdFilterOptions.getStatus().ifPresent(status ->
                    predicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.join("status").get("status")), "%" + status.toLowerCase() + "%"
                            )
                    )
            );

            // Convert List<Predicate> to an array of Predicate for criteriaBuilder.and()
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

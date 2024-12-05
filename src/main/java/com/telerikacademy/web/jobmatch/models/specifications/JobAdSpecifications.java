package com.telerikacademy.web.jobmatch.models.specifications;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
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
                String[] locations = location.split(",\\s*");
                List<Predicate> locationPredicates = new ArrayList<>();
                for (String loc : locations) {
                    locationPredicates.add(criteriaBuilder.equal(locationJoin.get("name"), loc.trim()));
                }
                Predicate combinedPredicate = criteriaBuilder.or(locationPredicates.toArray(new Predicate[0]));
                predicates.add(combinedPredicate);
            });

            jobAdFilterOptions.getStatus().ifPresent(status -> {
                String[] statuses = status.split(",\\s*"); // Split by ", " or just ","
                Join<JobAd, Status> statusJoin = root.join("status");
                List<Predicate> statusPredicates = new ArrayList<>();

                for (String stat : statuses) {
                    statusPredicates.add(
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(statusJoin.get("status")),
                                    "%" + stat.trim().toLowerCase() + "%"
                            )
                    );
                }

                // Combine all status predicates with an OR
                Predicate combinedStatusPredicate = criteriaBuilder.or(statusPredicates.toArray(new Predicate[0]));
                predicates.add(combinedStatusPredicate);
            });

            jobAdFilterOptions.getHybrid().ifPresent(hybrid ->
                        predicates.add(
                                criteriaBuilder.equal(root.get("hybrid"), hybrid)
                        )
            );


            // Convert List<Predicate> to an array of Predicate for criteriaBuilder.and()
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

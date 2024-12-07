package com.telerikacademy.web.jobmatch.models.specifications;

import com.telerikacademy.web.jobmatch.models.*;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobApplicationSpecification implements Specification<JobApplication> {
    private final JobApplicationFilterOptions filterOptions;

    public JobApplicationSpecification(JobApplicationFilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }

    @Override
    public Predicate toPredicate(@Nonnull Root<JobApplication> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();
        filterOptions.getMinSalary().ifPresent(minSalary ->
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxSalary"), minSalary)));


        filterOptions.getMaxSalary().ifPresent(maxSalary ->
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("minSalary"), maxSalary)));
        filterOptions.getCreator().ifPresent(creator -> {
            Join<JobApplication, Professional> professionalJoin = root.join("professional", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(professionalJoin.get("username"), creator));
        });
        filterOptions.getLocation().ifPresent(location -> {
            Join<JobAd, Location> locationJoin = root.join("location");
            String[] locations = location.split(",\\s*");
            List<Predicate> locationPredicates = new ArrayList<>();
            for (String loc : locations) {
                locationPredicates.add(criteriaBuilder.equal(locationJoin.get("name"), loc.trim()));
            }
            Predicate combinedPredicate = criteriaBuilder.or(locationPredicates.toArray(new Predicate[0]));
            predicates.add(combinedPredicate);
        });
        filterOptions.getStatus().ifPresent(status -> {
            String[] statuses = status.split(",\\s*");
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
            Predicate combinedStatusPredicate = criteriaBuilder.or(statusPredicates.toArray(new Predicate[0]));
            predicates.add(combinedStatusPredicate);
        });

        filterOptions.getHybrid().ifPresent(hybrid ->
                predicates.add(
                        criteriaBuilder.equal(root.get("hybrid"), hybrid)
                )
        );

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

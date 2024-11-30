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
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("minSalary"), minSalary)));
        filterOptions.getMaxSalary().ifPresent(maxSalary ->
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("maxSalary"), maxSalary)));
        filterOptions.getCreator().ifPresent(creator -> {
            Join<JobApplication, Professional> professionalJoin = root.join("professional", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(professionalJoin.get("username"), creator));
        });
        filterOptions.getLocation().ifPresent(location -> {
            Join<JobApplication, Location> locationJoin = root.join("location", JoinType.LEFT);
            Predicate locationPredicate = criteriaBuilder.equal(locationJoin.get("name"), location);
            Predicate homePredicate = criteriaBuilder.equal(locationJoin.get("name"), "Home");
            predicates.add(criteriaBuilder.or(locationPredicate, homePredicate));
        });
        filterOptions.getStatus().ifPresent(status -> {
            Join<JobApplication, Status> statusJoin = root.join("status", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(statusJoin.get("status"), status));
        });

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

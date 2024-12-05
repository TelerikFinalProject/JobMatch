package com.telerikacademy.web.jobmatch.models.filter_options;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class JobApplicationFilterOptions {
    private Optional<Double> minSalary;
    private Optional<Double> maxSalary;
    private Optional<String> location;
    private Optional<String> creator;
    private Optional<String> status;
    private Optional<Boolean> hybrid;

    public JobApplicationFilterOptions(Double minSalary,
                                       Double maxSalary,
                                       String creator,
                                       String location,
                                       String status,
                                       Boolean hybrid) {
        this.minSalary = Optional.ofNullable(minSalary);
        this.maxSalary = Optional.ofNullable(maxSalary);
        this.creator = Optional.ofNullable(creator);
        this.location = Optional.ofNullable(location);
        this.status = Optional.ofNullable(status);
        this.hybrid = Optional.ofNullable(hybrid);
    }
}

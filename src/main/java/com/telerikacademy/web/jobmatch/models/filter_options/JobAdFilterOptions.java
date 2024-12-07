package com.telerikacademy.web.jobmatch.models.filter_options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class JobAdFilterOptions {
    private Optional<String> positionTitle;
    private Optional<Double> minSalary;
    private Optional<Double> maxSalary;
    private Optional<String> location;
    private Optional<String> creator;
    private Optional<String> status;
    private Optional<Boolean> hybrid;

    public JobAdFilterOptions(String positionTitle,
                              Double minSalary,
                              Double maxSalary,
                              String location,
                              String creator,
                              String status,
                              Boolean hybrid) {
        this.positionTitle = Optional.ofNullable(positionTitle);
        this.minSalary = Optional.ofNullable(minSalary);
        this.maxSalary = Optional.ofNullable(maxSalary);
        this.location = Optional.ofNullable(location);
        this.creator = Optional.ofNullable(creator);
        this.status = Optional.ofNullable(status);
        this.hybrid = Optional.ofNullable(hybrid);
    }
}

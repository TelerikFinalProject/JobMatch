package com.telerikacademy.web.jobmatch.models.filter_options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobAdFilterOptions extends JobApplicationFilterOptions{
    private Optional<String> positionTitle;

    public JobAdFilterOptions(String positionTitle, Double minSalary, Double maxSalary, String location, String creator, String status) {
        super(minSalary, maxSalary, location, creator, status);
        this.positionTitle = Optional.ofNullable(positionTitle);
    }
}

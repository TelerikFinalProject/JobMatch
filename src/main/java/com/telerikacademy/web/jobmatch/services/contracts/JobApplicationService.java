package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;

import java.util.List;

public interface JobApplicationService {
    List<JobApplication> getJobApplications(JobApplicationFilterOptions filterOptions);

    JobApplication getJobApplication(int id);

    void addJobApplication(JobApplication jobAd);

    void updateJobApplication(JobApplication jobAd);

    void removeJobApplication(int id);

    List<JobApplication> getPaginatedJobApplications(List<JobApplication> jobApplications, int page, int size);
}

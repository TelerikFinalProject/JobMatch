package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.models.JobApplication;

import java.util.List;

public interface JobApplicationService {
    List<JobApplication> getJobApplications();

    JobApplication getJobApplication(int id);

    void addJobApplication(JobApplication jobAd);

    void updateJobApplication(JobApplication jobAd);

    void removeJobApplication(int id);
}

package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.models.JobApplication;

import java.util.List;

public interface JobApplicationRepository {
    List<JobApplication> findAll();

    JobApplication findById(int id);

    void save(JobApplication jobApplication);

    void update(JobApplication jobApplication);

    void delete(JobApplication jobApplication);

}

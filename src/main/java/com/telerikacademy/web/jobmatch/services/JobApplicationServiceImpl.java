package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.Exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.repositories.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Override
    public List<JobApplication> getJobApplications() {
        return jobApplicationRepository.findAll();
    }

    @Override
    public JobApplication getJobApplication(int id) {
        JobApplication jobApplication = jobApplicationRepository.findById(id);

        if (jobApplication == null) {
            throw new EntityNotFoundException("Job application", id);
        }

        return jobApplication;
    }

    @Override
    public void addJobApplication(JobApplication jobAd) {
        jobApplicationRepository.save(jobAd);
    }

    @Override
    public void updateJobApplication(JobApplication jobAd) {
        jobApplicationRepository.findById(jobAd.getId());

        jobApplicationRepository.update(jobAd);
    }

    @Override
    public void removeJobApplication(int id) {

    }
}

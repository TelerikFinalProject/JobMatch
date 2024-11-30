package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.models.specifications.JobApplicationSpecification;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobApplicationRepository;
import com.telerikacademy.web.jobmatch.services.contracts.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public List<JobApplication> getJobApplications(JobApplicationFilterOptions filterOptions) {
        Specification<JobApplication> spec = new JobApplicationSpecification(filterOptions);
        return jobApplicationRepository.findAll(spec);
    }

    @Override
    public JobApplication getJobApplication(int id) {

        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job application", id));
    }

    @Override
    public void addJobApplication(JobApplication jobApplication) {
        jobApplicationRepository.save(jobApplication);
    }

    @Override
    public void updateJobApplication(JobApplication jobApplication) {
        JobApplication existingJobApplication = jobApplicationRepository.findById(jobApplication.getId())
                .orElseThrow(() -> new EntityNotFoundException("Job application", jobApplication.getId()));

        jobApplicationRepository.save(jobApplication);
    }

    @Override
    public void removeJobApplication(int id) {

    }
}

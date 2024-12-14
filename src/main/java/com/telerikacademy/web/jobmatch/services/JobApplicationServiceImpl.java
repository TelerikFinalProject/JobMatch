package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.models.specifications.JobApplicationSpecification;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobApplicationRepository;
import com.telerikacademy.web.jobmatch.services.contracts.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        jobApplicationRepository.findById(jobApplication.getId())
                .orElseThrow(() -> new EntityNotFoundException("Job application", jobApplication.getId()));

        jobApplicationRepository.save(jobApplication);
    }

    @Override
    public void removeJobApplication(int id) {
        jobApplicationRepository.deleteById(id);
    }

    @Override
    public int getNumberOfActiveJobApplications() {
        return getJobApplications(new JobApplicationFilterOptions(null, null, null,
                null, "Active", null)).size();
    }

    @Override
    public List<JobApplication> getPaginatedJobApplications(List<JobApplication> jobApplications, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, jobApplications.size());
        return jobApplications.subList(start, end);
    }

    @Override
    public Set<Skill> getAllUniqueSkillsUsedInJobApplications() {
        List<JobApplication> allApplications = jobApplicationRepository.findAll();

        return allApplications
                .stream()
                .flatMap(application -> application.getQualifications().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public List<JobApplication> getAllBySkill(Skill skill) {
        return jobApplicationRepository.findAllBySkill(skill.getId());
    }
}

package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;

import java.util.List;
import java.util.Set;

public interface JobApplicationService {
    List<JobApplication> getJobApplications(JobApplicationFilterOptions filterOptions);

    JobApplication getJobApplication(int id);

    void addJobApplication(JobApplication jobAd);

    void updateJobApplication(JobApplication jobAd);

    void removeJobApplication(int id);

    int getNumberOfActiveJobApplications();

    List<JobApplication> getPaginatedJobApplications(List<JobApplication> jobApplications, int page, int size);

    Set<Skill> getAllUniqueSkillsUsedInJobApplications();

    List<JobApplication> getAllBySkill(Skill skill);
}

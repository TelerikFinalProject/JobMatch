package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;

import java.util.List;
import java.util.Set;

public interface JobAdService {

    List<JobAd> getJobAds(JobAdFilterOptions filterOptions);

    JobAd getJobAd(int id);

    JobAd createJobAd(JobAd jobAd);

    void updateJobAd(JobAd jobAd);

    void removeJobAd(int id);

    int getNumberOfActiveJobAds();

    List<JobAd> getJobAdsBySkill(Skill skill);

    List<JobAd> getPaginatedJobAds(List<JobAd> jobAds, int page, int size);

    Set<Skill> getAllUniqueSkillsUsedInJobAds();

    List<JobAd> getFeaturedAds();
}

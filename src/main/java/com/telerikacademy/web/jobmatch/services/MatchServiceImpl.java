package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import com.telerikacademy.web.jobmatch.services.contracts.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatchServiceImpl {
    private final JobAdService jobAdService;
    private final JobApplicationService jobApplicationService;

    @Autowired
    public MatchServiceImpl(JobAdService jobAdService, JobApplicationService jobApplicationService) {
        this.jobAdService = jobAdService;
        this.jobApplicationService = jobApplicationService;
    }

    public Set<JobAd> getSuitableAds(JobApplication application){
        List<JobAd> allAds = jobAdService.getJobAds(new JobAdFilterOptions());

        Set<JobAd> suitableAds = new HashSet<>();
        for (JobAd ad : allAds) {
            //TODO check skills and salary and location
            suitableAds.add(ad);
        }
        return suitableAds;
    }

    public Set<JobApplication> getSuitableApplications(JobAd ad) {
        double minSalary = ad.getMinSalary() - (ad.getMinSalary() * 20 / 100);
        double maxSalary = ad.getMaxSalary() + (ad.getMaxSalary() * 20 / 100);
        String location = ad.getLocation().getName();
        String status = "Active";

        JobApplicationFilterOptions filterOptions =
                new JobApplicationFilterOptions(minSalary, maxSalary, null, location, status);
        List<JobApplication> filteredApplications = jobApplicationService.getJobApplications(filterOptions);

        Set<JobApplication> suitableApplications = new HashSet<>();
        double minQuantitySkills = ad.getSkills().size() * 90.0 / 100;

        for (JobApplication application : filteredApplications) {
            if (application.getQualifications().size() < minQuantitySkills) {
                continue;
            }
            double mutualSkills = 0;
            for (Skill requirement : ad.getSkills()) {
                if (application.getQualifications().contains(requirement)) {
                    mutualSkills++;
                }
            }
            if (mutualSkills >= minQuantitySkills) {
                suitableApplications.add(application);
            }
        }
        return suitableApplications;
    }
}

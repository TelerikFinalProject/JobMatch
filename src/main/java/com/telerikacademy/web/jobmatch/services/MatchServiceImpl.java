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
        List<JobAd> allAds = jobAdService.getJobAds();

        Set<JobAd> suitableAds = new HashSet<>();
        for (JobAd ad : allAds) {
            //TODO check skills and salary and location
            suitableAds.add(ad);
        }
        return suitableAds;
    }

    public Set<JobApplication> getSuitableApplications(JobAd ads){
        List<JobApplication> allApplications = jobApplicationService.getJobApplications();

        Set<JobApplication> suitableApplications = new HashSet<>();
        for (JobApplication application : allApplications) {
            //TODO check skills and salary and location
            suitableApplications.add(application);
        }
        return suitableApplications;
    }
}

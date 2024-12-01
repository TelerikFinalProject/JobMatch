package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;

import java.util.Set;

public interface MatchService {
    Set<JobApplication> getSuitableApplications(JobAd ad);

    Set<JobAd> getSuitableAds(JobApplication application);
}

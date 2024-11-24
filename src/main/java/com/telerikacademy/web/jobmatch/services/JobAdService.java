package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.models.JobAd;

import java.util.List;

public interface JobAdService {

    List<JobAd> getJobAds();

    JobAd getJobAd(int id);

    void addJobAd(JobAd jobAd);

    void updateJobAd(JobAd jobAd);

    void removeJobAd(int id);
}

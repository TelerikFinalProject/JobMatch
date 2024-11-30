package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoIn;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;

import java.util.List;

public interface JobAdService {

    List<JobAd> getJobAds(JobAdFilterOptions filterOptions);

    JobAd getJobAd(int id);

    JobAd createJobAd(JobAd jobAd);

    void updateJobAd(JobAd jobAd);

    void removeJobAd(int id);
}

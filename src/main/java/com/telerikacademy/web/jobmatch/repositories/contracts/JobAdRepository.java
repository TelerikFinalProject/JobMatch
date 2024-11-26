package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;

import java.util.List;

public interface JobAdRepository {

    List<JobAd> findAll();

    JobAd findById(int id);

    void save(JobAd jobAd);

    void update(JobAd jobAd);

    void delete(JobAd jobAd);
}

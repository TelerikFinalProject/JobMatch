package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobAdRepository;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobAdServiceImpl implements JobAdService {

    private final JobAdRepository jobAdRepository;

    public JobAdServiceImpl(JobAdRepository jobAdRepository) {
        this.jobAdRepository = jobAdRepository;
    }

    @Override
    public List<JobAd> getJobAds() {
        return jobAdRepository.findAll();
    }

    @Override
    public JobAd getJobAd(int id) {
        JobAd jobAd =  jobAdRepository.findById(id);

        if (jobAd == null) {
            throw new EntityNotFoundException("Job ad", id);
        }

        return jobAd;
    }

    @Override
    public void addJobAd(JobAd jobAd) {
        jobAdRepository.save(jobAd);
    }

    @Override
    public void updateJobAd(JobAd jobAd) {
        jobAdRepository.update(jobAd);
    }

    @Override
    public void removeJobAd(int id) {
        JobAd jobAd = jobAdRepository.findById(id);
        jobAdRepository.delete(jobAd);
    }
}

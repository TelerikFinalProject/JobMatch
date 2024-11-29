package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobAdRepository;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAdServiceImpl implements JobAdService {

    private final JobAdRepository jobAdRepository;

    @Override
    public List<JobAd> getJobAds() {
        return jobAdRepository.findAll();
    }

    @Override
    public JobAd getJobAd(int id) {

        return jobAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job ad", id));
    }

    @Override
    public void addJobAd(JobAd jobAd) {
        jobAdRepository.save(jobAd);
    }

    @Override
    public void updateJobAd(JobAd jobAd) {
        JobAd existingAd = getJobAd(jobAd.getId());

        jobAdRepository.save(jobAd);
    }

    @Override
    public void removeJobAd(int id) {
        jobAdRepository.deleteById(id);
    }
}

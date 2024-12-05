package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.specifications.JobAdSpecifications;
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
    public List<JobAd> getJobAds(JobAdFilterOptions filterOptions) {
        return jobAdRepository.findAll(JobAdSpecifications.filterByOptions(filterOptions));
    }

    @Override
    public JobAd getJobAd(int id) {

        return jobAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job ad", id));
    }

    @Override
    public JobAd createJobAd(JobAd jobAd) {
        return jobAdRepository.save(jobAd);
    }

    @Override
    public void updateJobAd(JobAd jobAd) {
        getJobAd(jobAd.getId());

        jobAdRepository.save(jobAd);
    }

    @Override
    public void removeJobAd(int id) {
        jobAdRepository.deleteById(id);
    }
}

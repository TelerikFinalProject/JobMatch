package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.specifications.JobAdSpecifications;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobAdRepository;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public int getNumberOfActiveJobAds() {
        return getJobAds(new JobAdFilterOptions(null, null,
                null, null, null, "Active", null)).size();
    }

    @Override
    public List<JobAd> getJobAdsBySkill(Skill skill) {
        return jobAdRepository.findAllBySkill(skill.getId());
    }

    @Override
    public List<JobAd> getPaginatedJobAds(List<JobAd> jobAds, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, jobAds.size());
        return jobAds.subList(start, end);
    }

    @Override
    public Set<Skill> getAllUniqueSkillsUsedInJobAds() {
        List<JobAd> allAds = jobAdRepository.findAll();

        return allAds
                .stream()
                .flatMap(jobAd -> jobAd.getSkills().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public List<JobAd> getFeaturedAds() {
        return jobAdRepository.findFeaturedJobAds();
    }
}

package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.EntityStatusException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobMatchRepository;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatchServiceImpl implements MatchService {
    private final JobAdService jobAdService;
    private final JobApplicationService jobApplicationService;
    private final StatusService statusService;
    private final ProfessionalService professionalService;
    private final JobMatchRepository jobMatchRepository;

    @Autowired
    public MatchServiceImpl(JobAdService jobAdService,
                            JobApplicationService jobApplicationService,
                            ProfessionalService professionalService,
                            StatusService statusService,
                            JobMatchRepository jobMatchRepository) {
        this.jobAdService = jobAdService;
        this.jobApplicationService = jobApplicationService;
        this.professionalService = professionalService;
        this.statusService = statusService;
        this.jobMatchRepository = jobMatchRepository;
    }

    public Set<JobAd> getSuitableAds(JobApplication application) {
        checkEntityStatus(application.getStatus().getStatus(), "Job application");

        double minSalary = application.getMinSalary() - (application.getMinSalary() * 20 / 100);
        double maxSalary = application.getMaxSalary() + (application.getMaxSalary() * 20 / 100);
        String location = String.format("%s, %s", application.getLocation().getName(), "Home");
        String status = "Active";

        JobAdFilterOptions filterOptions =
                new JobAdFilterOptions(null, minSalary, maxSalary, location, null, status);

        List<JobAd> filteredAds = jobAdService.getJobAds(filterOptions);

        Set<JobAd> suitableAds = new HashSet<>();
        for (JobAd ad : filteredAds) {
            double minQuantitySkills = ad.getSkills().size() * 50.0 / 100;

            if (application.getQualifications().size() < minQuantitySkills) {
                continue;
            }
            double mutualSkills = 0;

            for (Skill qualification : application.getQualifications()) {
                if (ad.getSkills().contains(qualification)) {
                    mutualSkills++;
                }
            }
            if (mutualSkills >= minQuantitySkills) {
                suitableAds.add(ad);
            }
        }
        return suitableAds;
    }

    public Set<JobApplication> getSuitableApplications(JobAd ad) {
        checkEntityStatus(ad.getStatus().getStatus(), "Job ad");

        double minSalary = ad.getMinSalary() - (ad.getMinSalary() * 20 / 100);
        double maxSalary = ad.getMaxSalary() + (ad.getMaxSalary() * 20 / 100);

        String location = ad.getLocation().getName();
        if (location.equals("Home")) {
            location = null;
        }

        String status = "Active";

        JobApplicationFilterOptions filterOptions =
                new JobApplicationFilterOptions(minSalary, maxSalary, null, location, status);
        List<JobApplication> filteredApplications = jobApplicationService.getJobApplications(filterOptions);

        Set<JobApplication> suitableApplications = new HashSet<>();
        double minQuantitySkills = ad.getSkills().size() * 50.0 / 100;

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

    @Override
    public JobApplication approveJobApplication(JobAd jobAd, JobApplication jobApplicationToApprove) {
        checkIfApplicationRequestIsValid(jobAd, jobApplicationToApprove);

        //Change status of Professional, add the Employer in a successful matches set
        approvalProcess(jobAd, jobApplicationToApprove);

        return jobApplicationToApprove;
    }

    @Override
    public JobAd approveJobAd(JobAd jobAd, JobApplication jobApplicationToApprove) {
        checkIfAdRequestIsValid(jobAd, jobApplicationToApprove);

        //Change status of Professional, add the Employer in a successful matches set
        approvalProcess(jobAd, jobApplicationToApprove);

        return jobAd;
    }

    @Override
    public void declineJobApplication(JobAd jobAd, JobApplication jobApplicationToDecline) {
        checkIfApplicationRequestIsValid(jobAd, jobApplicationToDecline);

        jobAd.getMatchRequestedApplications().remove(jobApplicationToDecline);
        jobAdService.updateJobAd(jobAd);
    }

    @Override
    public void declineAd(JobAd jobAdToDecline, JobApplication jobApplication) {
        checkIfAdRequestIsValid(jobAdToDecline, jobApplication);

        jobApplication.getMatchRequestedAds().remove(jobAdToDecline);
        jobApplicationService.updateJobApplication(jobApplication);
    }

    @Override
    public void deleteJobAdRequests(JobAd jobAd) {
        jobMatchRepository.deleteJobAdInJobAppReq(jobAd.getId());
        jobMatchRepository.deleteJobAdInJobAdReq(jobAd.getId());
    }

    @Override
    public void deleteJobApplicationRequests(JobApplication jobApplication) {
        jobMatchRepository.deleteJobAppInJobAdReq(jobApplication.getId());
        jobMatchRepository.deleteJobAppInJobAppReq(jobApplication.getId());
    }

    private void approvalProcess(JobAd jobAd, JobApplication jobApplicationToApprove) {
        Professional applicant = jobApplicationToApprove.getProfessional();
        applicant.setStatus(statusService.getStatus("Busy"));
        applicant.getSuccessfulMatches().add(jobAd.getEmployer());
        professionalService.updateProfessional(applicant);

        deleteJobApplicationRequests(jobApplicationToApprove);
        deleteJobAdRequests(jobAd);

        jobAd.setMatchesSentToJobApplications(new HashSet<>());
        jobAd.setMatchRequestedApplications(new HashSet<>());
        jobAd.setStatus(statusService.getStatus("Archived"));
        jobAdService.updateJobAd(jobAd);

        jobApplicationToApprove.setMatchesSentToJobAds(new HashSet<>());
        jobApplicationToApprove.setMatchRequestedAds(new HashSet<>());
        jobApplicationToApprove.setStatus(statusService.getStatus("Matched"));
        jobApplicationService.updateJobApplication(jobApplicationToApprove);
    }

    private static void checkIfApplicationRequestIsValid(JobAd jobAd, JobApplication jobApplicationToDecline) {
        if (!jobAd.getMatchRequestedApplications().contains(jobApplicationToDecline)) {
            throw new EntityNotFoundException("No such application request exists!");
        }
    }

    private static void checkIfAdRequestIsValid(JobAd jobAd, JobApplication jobApplicationToDecline) {
        if (!jobApplicationToDecline.getMatchRequestedAds().contains(jobAd)) {
            throw new EntityNotFoundException("No such application request exists!");
        }
    }

    private static void checkEntityStatus(String status, String entityType) {
        if (!status.equals("Active")) {
            throw new EntityStatusException(entityType, status);
        }
    }


}

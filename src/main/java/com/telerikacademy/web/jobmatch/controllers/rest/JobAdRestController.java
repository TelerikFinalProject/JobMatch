package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.EntityStatusException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
import com.telerikacademy.web.jobmatch.helpers.JobAdMappers;
import com.telerikacademy.web.jobmatch.helpers.JobApplicationMappers;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoOut;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoOut;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/job-ads")
public class JobAdRestController {

    private final JobAdService jobAdService;
    private final JobApplicationService jobApplicationService;
    private final EmployersService employersService;
    private final LocationService locationService;
    private final StatusService statusService;
    private final MatchService matchService;
    private final SkillService skillService;

    public JobAdRestController(JobAdService jobAdService,
                               EmployersService employersService,
                               LocationService locationService,
                               StatusService statusService,
                               MatchService matchService,
                               JobApplicationService jobApplicationService,
                               SkillService skillService) {
        this.jobAdService = jobAdService;
        this.employersService = employersService;
        this.locationService = locationService;
        this.statusService = statusService;
        this.matchService = matchService;
        this.jobApplicationService = jobApplicationService;
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<List<JobAdDtoOut>> getJobAds(@RequestParam(required = false) String positionTitle,
                                                       @RequestParam(required = false) Double minSalary,
                                                       @RequestParam(required = false) Double maxSalary,
                                                       @RequestParam(required = false) String location,
                                                       @RequestParam(required = false) String creator,
                                                       @RequestParam(required = false) String status) {

        JobAdFilterOptions filterOptions = new JobAdFilterOptions(positionTitle, minSalary, maxSalary, location, creator, status);
        List<JobAd> jobAds = jobAdService.getJobAds(filterOptions);
        List<JobAdDtoOut> jobAdDtoOuts = JobAdMappers.INSTANCE.toDtoOutList(jobAds);

        return ResponseEntity.ok(jobAdDtoOuts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobAdDtoOut> getJobAdById(@PathVariable int id) {
        try {
            JobAd jobAd = jobAdService.getJobAd(id);
            JobAdDtoOut jobAdDtoOut = JobAdMappers.INSTANCE.toDtoOut(jobAd);

            return ResponseEntity.ok(jobAdDtoOut);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<JobAdDtoOut> createJobAd(Authentication authentication, @Valid @RequestBody JobAdDtoIn jobAdDtoIn) {

        try {
            JobAd jobAd = JobAdMappers.INSTANCE.fromDtoIn(jobAdDtoIn, locationService, statusService, skillService);
            Employer employer = employersService.getEmployer(authentication.getName());
            jobAd.setEmployer(employer);
            jobAdService.createJobAd(jobAd);
            JobAdDtoOut jobAdDtoOut = JobAdMappers.INSTANCE.toDtoOut(jobAd);
            return ResponseEntity.status(HttpStatus.CREATED).body(jobAdDtoOut);
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobAdDtoOut> updateJobAd(@PathVariable int id, @RequestBody JobAdDtoIn jobAdDtoIn) {
        JobAd jobAd;
        //TODO
        try {
            jobAd = jobAdService.getJobAd(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        jobAdService.updateJobAd(jobAd);
        return ResponseEntity.ok(JobAdMappers.INSTANCE.toDtoOut(jobAd));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobAd(@PathVariable int id) {
        jobAdService.removeJobAd(id);
        return ResponseEntity.ok("Job application has been deleted");
    }

    @GetMapping("/{id}/suitable-applications")
    public ResponseEntity<Set<JobApplicationDtoOut>> getSuitableApplications(@PathVariable int id) {
        try {
            JobAd jobAd = jobAdService.getJobAd(id);

            Set<JobApplication> suitableJobApplications = matchService.getSuitableApplications(jobAd);
            return ResponseEntity.ok(JobApplicationMappers.INSTANCE.toDtoOutSet(suitableJobApplications));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{jobAdId}/request-match/{jobApplicationId}")
    public ResponseEntity<Set<JobApplicationDtoOut>> requestMatch(@PathVariable int jobAdId, @PathVariable int jobApplicationId) {
        try {
            JobAd jobAdToMatchWith = jobAdService.getJobAd(jobAdId);
            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            if (!matchService.getSuitableApplications(jobAdToMatchWith).contains(jobApplication)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Job %s with ID:%d is not suitable for your %s!", "application", jobApplicationId, "ad"));
            }

            if (!jobAdToMatchWith.getMatchesSentToJobApplications().add(jobApplication)){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A match request for Job %s with ID:%d has already been initiated!", "application", jobApplicationId));
            }

            if (jobAdToMatchWith.getMatchRequestedApplications().contains(jobApplication)){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A match request for Job %s with ID:%d already exists, " +
                                "please reach out to the Job %s creator!", "application", jobAdId, "application"));
            }

            jobAdService.updateJobAd(jobAdToMatchWith);
            return ResponseEntity.ok(JobApplicationMappers.INSTANCE.toDtoOutSet(jobAdToMatchWith.getMatchesSentToJobApplications()));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{adId}/requests/{applicationId}/approve")
    public ResponseEntity<JobApplicationDtoOut> approveJobAd(@PathVariable int adId, @PathVariable int applicationId) {
        try {
            JobAd jobAd = jobAdService.getJobAd(adId);
            JobApplication jobApplicationToApprove = jobApplicationService.getJobApplication(applicationId);

            return ResponseEntity.ok(JobApplicationMappers.INSTANCE
                    .toDtoOut(matchService.approveJobApplication(jobAd, jobApplicationToApprove)));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{adId}/requests/{applicationId}/decline")
    public ResponseEntity<String> declineJobAd(@PathVariable int adId, @PathVariable int applicationId) {
        try {
            JobAd jobAd = jobAdService.getJobAd(adId);
            JobApplication jobApplicationToDecline = jobApplicationService.getJobApplication(applicationId);

            matchService.declineJobApplication(jobAd, jobApplicationToDecline);
            return ResponseEntity.ok("Job application has been declined");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

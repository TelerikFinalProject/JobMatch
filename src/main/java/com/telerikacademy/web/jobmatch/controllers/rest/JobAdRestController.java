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
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoUpdate;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoOut;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.services.TweeterClient;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-ads")
public class JobAdRestController {

    private final JobAdService jobAdService;
    private final JobApplicationService jobApplicationService;
    private final EmployersService employersService;
    private final LocationService locationService;
    private final StatusService statusService;
    private final MatchService matchService;
    private final SkillService skillService;
    private final MailService mailService;

    @PreAuthorize("hasAnyRole('ROLE_PROFESSIONAL', 'ROLE_EMPLOYER', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<JobAdDtoOut>> getJobAds(@RequestParam(required = false) String positionTitle,
                                                       @RequestParam(required = false) Double minSalary,
                                                       @RequestParam(required = false) Double maxSalary,
                                                       @RequestParam(required = false) String location,
                                                       @RequestParam(required = false) String creator,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) Boolean hybrid,
                                                       Authentication authentication) {

        JobAdFilterOptions filterOptions;
        boolean isEmployer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYER"));

        if (isEmployer) {
            Employer employer = employersService.getEmployer(authentication.getName());
            filterOptions = new JobAdFilterOptions(positionTitle, minSalary, maxSalary, location, employer.getCompanyName(), status, hybrid);
        } else {
            filterOptions = new JobAdFilterOptions(positionTitle, minSalary, maxSalary, location, creator, status, hybrid);
        }

        List<JobAd> jobAds = jobAdService.getJobAds(filterOptions);
        List<JobAdDtoOut> jobAdDtoOuts = JobAdMappers.INSTANCE.toDtoOutList(jobAds);

        return ResponseEntity.ok(jobAdDtoOuts);
    }

    @PreAuthorize("hasAnyRole('ROLE_PROFESSIONAL', 'ROLE_EMPLOYER', 'ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<JobAdDtoOut> getJobAdById(@PathVariable int id,
                                                    Authentication authentication) {

        boolean isEmployer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYER"));

        try {
            JobAd jobAd = jobAdService.getJobAd(id);

            if (isEmployer) {
                Employer employer = employersService.getEmployer(authentication.getName());

                if (!jobAd.getEmployer().getCompanyName().equals(employer.getCompanyName())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }
            JobAdDtoOut jobAdDtoOut = JobAdMappers.INSTANCE.toDtoOut(jobAd);

            return ResponseEntity.ok(jobAdDtoOut);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<JobAdDtoOut> createJobAd(Authentication authentication, @Valid @RequestBody JobAdDtoIn jobAdDtoIn) {

        try {
            JobAd jobAd = JobAdMappers.INSTANCE.fromDtoIn(jobAdDtoIn, locationService, statusService, skillService);
            Employer employer = employersService.getEmployer(authentication.getName());
            jobAd.setEmployer(employer);
            jobAdService.createJobAd(jobAd);
            JobAdDtoOut jobAdDtoOut = JobAdMappers.INSTANCE.toDtoOut(jobAd);

            TweeterClient tweeterClient = new TweeterClient();
            tweeterClient.sendTweet(String.format("Job ad '%s' has been added from company '%s'"
                    , jobAd.getPositionTitle(), jobAd.getEmployer().getCompanyName()));

            return ResponseEntity.status(HttpStatus.CREATED).body(jobAdDtoOut);
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYER', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<JobAdDtoOut> updateJobAd(@PathVariable int id,
                                                   @RequestBody JobAdDtoUpdate jobAdDtoUpdate,
                                                   Authentication authentication) {
        boolean isEmployer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYER"));

        try {
            JobAd jobAd = jobAdService.getJobAd(id);

            if (isEmployer) {
                Employer employer = employersService.getEmployer(authentication.getName());

                if (!jobAd.getEmployer().getCompanyName().equals(employer.getCompanyName())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }

            JobAd updatedJobAd = JobAdMappers.INSTANCE.fromDtoIn(jobAd, jobAdDtoUpdate,
                    locationService, statusService, skillService);
            jobAdService.updateJobAd(updatedJobAd);
            return ResponseEntity.ok(JobAdMappers.INSTANCE.toDtoOut(updatedJobAd));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobAd(@PathVariable int id,
                                              Authentication authentication) {
        boolean isEmployer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYER"));

        try {
            JobAd jobAd = jobAdService.getJobAd(id);

            if (isEmployer) {
                Employer employer = employersService.getEmployer(authentication.getName());

                if (!jobAd.getEmployer().getCompanyName().equals(employer.getCompanyName())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }

            jobAdService.removeJobAd(id);
            return ResponseEntity.ok("Job application has been deleted");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_EMPLOYER', 'ROLE_ADMIN')")
    @GetMapping("/{id}/suitable-applications")
    public ResponseEntity<Set<JobApplicationDtoOut>> getSuitableApplications(@PathVariable int id,
                                                                             Authentication authentication) {

        boolean isEmployer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_EMPLOYER"));
        try {
            JobAd jobAd = jobAdService.getJobAd(id);

            if (isEmployer) {
                Employer employer = employersService.getEmployer(authentication.getName());

                if (!jobAd.getEmployer().getCompanyName().equals(employer.getCompanyName())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }

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

            if (!jobAdToMatchWith.getMatchesSentToJobApplications().add(jobApplication)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A match request for Job %s with ID:%d has already been initiated!", "application", jobApplicationId));
            }

            if (jobAdToMatchWith.getMatchRequestedApplications().contains(jobApplication)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A match request for Job %s with ID:%d already exists, " +
                                "please reach out to the Job %s creator!", "application", jobAdId, "application"));
            }

            mailService.sendNotificationViaEmailToApplicant(jobApplication, jobAdToMatchWith);

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

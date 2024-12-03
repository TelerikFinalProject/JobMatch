package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.EntityStatusException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
import com.telerikacademy.web.jobmatch.helpers.JobAdMappers;
import com.telerikacademy.web.jobmatch.helpers.JobApplicationMappers;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoOut;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoOut;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoUpdate;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationRestController {

    private final JobApplicationService jobApplicationService;
    private final JobAdService jobAdService;
    private final LocationService locationService;
    private final StatusService statusService;
    private final ProfessionalService professionalService;
    private final SkillService skillService;
    private final MatchService matchService;
    private final MailService mailService;

    @Autowired
    public JobApplicationRestController(JobApplicationService jobApplicationService,
                                        LocationService locationService,
                                        StatusService statusService,
                                        ProfessionalService professionalService,
                                        SkillService skillService,
                                        MatchService matchService,
                                        JobAdService jobAdService,
                                        MailService mailService) {
        this.jobApplicationService = jobApplicationService;
        this.locationService = locationService;
        this.statusService = statusService;
        this.professionalService = professionalService;
        this.skillService = skillService;
        this.matchService = matchService;
        this.jobAdService = jobAdService;
        this.mailService = mailService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSIONAL', 'ROLE_EMPLOYER')")
    @GetMapping
    public ResponseEntity<List<JobApplicationDtoOut>> getJobApplications(@RequestParam(required = false) Double minSalary,
                                                                         @RequestParam(required = false) Double maxSalary,
                                                                         @RequestParam(required = false) String creator,
                                                                         @RequestParam(required = false) String location,
                                                                         @RequestParam(required = false) String status,
                                                                         Authentication authentication) {

        boolean isProfessional = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROFESSIONAL"));
        JobApplicationFilterOptions filterOptions;

        if (isProfessional) {
            filterOptions = new JobApplicationFilterOptions(minSalary, maxSalary, authentication.getName(), location, status);
        } else {
            filterOptions = new JobApplicationFilterOptions(minSalary, maxSalary, creator, location, status);
        }

        List<JobApplication> jobApplications = jobApplicationService.getJobApplications(filterOptions);

        List<JobApplicationDtoOut> jobApplicationDtoOuts = JobApplicationMappers.INSTANCE.toDtoOutList(jobApplications);

        return ResponseEntity.ok(jobApplicationDtoOuts);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSIONAL', 'ROLE_EMPLOYER')")
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDtoOut> getJobApplicationById(@PathVariable int id,
                                                                      Authentication authentication) {

        boolean isProfessional = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROFESSIONAL"));
        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            if (isProfessional) {
                Professional professional = professionalService.getProfessionalByUsername(authentication.getName());
                if (!jobApplication.getProfessional().getUsername().equals(professional.getUsername())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            JobApplicationDtoOut jobApplicationDtoOut = JobApplicationMappers.INSTANCE.toDtoOut(jobApplication);

            return ResponseEntity.ok(jobApplicationDtoOut);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSIONAL')")
    @PostMapping
    public ResponseEntity<JobApplicationDtoOut> createJobApplication(@Valid @RequestBody JobApplicationDtoIn jobApplicationDtoIn,
                                                                     Authentication authentication) {
        try {
            JobApplication jobApplication =
                    JobApplicationMappers.INSTANCE.fromDtoIn(jobApplicationDtoIn, statusService, locationService, skillService);
            jobApplication.setProfessional(professionalService.getProfessionalByUsername(authentication.getName()));

            jobApplicationService.addJobApplication(jobApplication);
            JobApplicationDtoOut jobApplicationDtoOut = JobApplicationMappers.INSTANCE.toDtoOut(jobApplication);
            return ResponseEntity.ok(jobApplicationDtoOut);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSIONAL')")
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDtoOut> updateJobApplication(@PathVariable int id,
                                                                     @RequestBody JobApplicationDtoUpdate jobApplicationDtoUpdate,
                                                                     Authentication authentication) {

        boolean isProfessional = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROFESSIONAL"));

        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            if (isProfessional) {
                Professional professional = professionalService.getProfessionalByUsername(authentication.getName());
                if (!jobApplication.getProfessional().getUsername().equals(professional.getUsername())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            JobApplication updatedJobApplication = JobApplicationMappers.INSTANCE.fromDtoIn(jobApplication,
                    jobApplicationDtoUpdate, statusService, locationService, skillService);
            jobApplicationService.updateJobApplication(updatedJobApplication);

            return ResponseEntity.ok(JobApplicationMappers.INSTANCE.toDtoOut(updatedJobApplication));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSIONAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobApplication(@PathVariable int id,
                                                       Authentication authentication) {
        JobApplication jobApplication = jobApplicationService.getJobApplication(id);

        boolean isProfessional = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROFESSIONAL"));

        if (isProfessional) {
            Professional professional = professionalService.getProfessionalByUsername(authentication.getName());
            if (!jobApplication.getProfessional().getUsername().equals(professional.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        jobApplicationService.removeJobApplication(id);
        return ResponseEntity.ok("Job application has been deleted");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSIONAL')")
    @GetMapping("/{id}/suitable-ads")
    public ResponseEntity<Set<JobAdDtoOut>> getSuitableAds(@PathVariable int id,
                                                           Authentication authentication) {

        boolean isProfessional = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROFESSIONAL"));

        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            if (isProfessional) {
                Professional professional = professionalService.getProfessionalByUsername(authentication.getName());
                if (!jobApplication.getProfessional().getUsername().equals(professional.getUsername())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            Set<JobAd> suitableJobAds = matchService.getSuitableAds(jobApplication);
            return ResponseEntity.ok(JobAdMappers.INSTANCE.toDtoOutSet(suitableJobAds));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityStatusException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{jobApplicationId}/request-match/{jobAdId}")
    public ResponseEntity<Set<JobAdDtoOut>> requestMatch(@PathVariable int jobApplicationId, @PathVariable int jobAdId) {
        try {
            JobAd jobAdToMatchWith = jobAdService.getJobAd(jobAdId);
            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            if (!matchService.getSuitableAds(jobApplication).contains(jobAdToMatchWith)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Job %s with ID:%d is not suitable for your %s!", "ad", jobAdId, "application"));
            }

            if (!jobApplication.getMatchesSentToJobAds().add(jobAdToMatchWith)){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A match request for Job %s with ID:%d has already been initiated!", "ad", jobAdId));
            }

            if (jobApplication.getMatchRequestedAds().contains(jobAdToMatchWith)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A match request for Job %s with ID:%d already exists, " +
                                "please reach out to the Job %s creator!", "ad", jobAdId, "ad"));
            }

            mailService.sendNotificationViaEmailToEmployer(jobApplication, jobAdToMatchWith);
            jobApplicationService.updateJobApplication(jobApplication);
            return ResponseEntity.ok(JobAdMappers.INSTANCE.toDtoOutSet(jobApplication.getMatchesSentToJobAds()));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{applicationId}/requests/{adId}/approve")
    public ResponseEntity<JobAdDtoOut> approveJobAd(@PathVariable int applicationId, @PathVariable int adId) {
        try {
            JobAd jobAd = jobAdService.getJobAd(adId);
            JobApplication jobApplicationToApprove = jobApplicationService.getJobApplication(applicationId);

            return ResponseEntity.ok(JobAdMappers.INSTANCE
                    .toDtoOut(matchService.approveJobAd(jobAd, jobApplicationToApprove)));

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{applicationId}/requests/{adId}/decline")
    public ResponseEntity<String> declineJobAd(@PathVariable int applicationId, @PathVariable int adId) {
        try {
            JobAd jobAd = jobAdService.getJobAd(adId);
            JobApplication jobApplicationToDecline = jobApplicationService.getJobApplication(applicationId);

            matchService.declineAd(jobAd, jobApplicationToDecline);
            return ResponseEntity.ok("Job ad has been declined");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

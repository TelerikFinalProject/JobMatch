package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
import com.telerikacademy.web.jobmatch.helpers.JobAdMappers;
import com.telerikacademy.web.jobmatch.helpers.JobApplicationMappers;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoOut;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoOut;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationRestController {

    private final JobApplicationService jobApplicationService;
    private final LocationService locationService;
    private final StatusService statusService;
    private final ProfessionalService professionalService;
    private final SkillService skillService;
    private final MatchService matchService;

    @Autowired
    public JobApplicationRestController(JobApplicationService jobApplicationService,
                                        LocationService locationService,
                                        StatusService statusService,
                                        ProfessionalService professionalService,
                                        SkillService skillService,
                                        MatchService matchService) {
        this.jobApplicationService = jobApplicationService;
        this.locationService = locationService;
        this.statusService = statusService;
        this.professionalService = professionalService;
        this.skillService = skillService;
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationDtoOut>> getJobApplications(@RequestParam(required = false) Double minSalary,
                                                                         @RequestParam(required = false) Double maxSalary,
                                                                         @RequestParam(required = false) String creator,
                                                                         @RequestParam(required = false) String location,
                                                                         @RequestParam(required = false) String status) {
        JobApplicationFilterOptions filterOptions =
                new JobApplicationFilterOptions(minSalary, maxSalary, creator, location, status);

        List<JobApplication> jobApplications = jobApplicationService.getJobApplications(filterOptions);

        List<JobApplicationDtoOut> jobApplicationDtoOuts = JobApplicationMappers.INSTANCE.toDtoOutList(jobApplications);

        return ResponseEntity.ok(jobApplicationDtoOuts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDtoOut> getJobApplicationById(@PathVariable int id) {
        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);
            JobApplicationDtoOut jobApplicationDtoOut = JobApplicationMappers.INSTANCE.toDtoOut(jobApplication);

            return ResponseEntity.ok(jobApplicationDtoOut);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDtoOut> updateJobApplication(@PathVariable int id,
                                                                     @RequestBody JobApplicationDtoIn jobApplicationDtoIn) {
        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            JobApplication updatedJobApplication = JobApplicationMappers.INSTANCE.fromDtoIn(jobApplication,
                    jobApplicationDtoIn, statusService, locationService, skillService);
            jobApplicationService.updateJobApplication(updatedJobApplication);

            return ResponseEntity.ok(JobApplicationMappers.INSTANCE.toDtoOut(updatedJobApplication));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobApplication(@PathVariable int id) {
        jobApplicationService.removeJobApplication(id);
        return ResponseEntity.ok("Job application has been deleted");
    }

    @GetMapping("/{id}/suitable-ads")
    public ResponseEntity<Set<JobAdDtoOut>> getSuitableAds(@PathVariable int id) {
        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);
            //TODO check if logged user is application's owner or admin

            Set<JobAd> suitableJobAds = matchService.getSuitableAds(jobApplication);
            return ResponseEntity.ok(JobAdMappers.INSTANCE.toDtoOutSet(suitableJobAds));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

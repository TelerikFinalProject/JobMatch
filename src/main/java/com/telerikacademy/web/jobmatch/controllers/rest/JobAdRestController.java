package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/job-ads")
public class JobAdRestController {

    private final JobAdService jobAdService;
    private final EmployersService employersService;
    private final LocationService locationService;
    private final StatusService statusService;
    private final MatchService matchService;

    public JobAdRestController(JobAdService jobAdService,
                               EmployersService employersService,
                               LocationService locationService,
                               StatusService statusService,
                               MatchService matchService) {
        this.jobAdService = jobAdService;
        this.employersService = employersService;
        this.locationService = locationService;
        this.statusService = statusService;
        this.matchService = matchService;
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
            JobAd jobAd = JobAdMappers.INSTANCE.fromDtoIn(jobAdDtoIn, locationService, statusService);
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
    public ResponseEntity<JobAd> updateJobAd(@PathVariable int id, @RequestBody JobAd jobAd) {
        jobAd.setId(id);
        jobAdService.updateJobAd(jobAd);
        return ResponseEntity.ok(jobAd);
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
            //TODO check if logged user is ad's owner or admin

            Set<JobApplication> suitableJobApplications = matchService.getSuitableApplications(jobAd);
            return ResponseEntity.ok(JobApplicationMappers.INSTANCE.toDtoOutSet(suitableJobApplications));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.Exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.services.JobAdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/job-ads")
public class JobAdRestController {

    private final JobAdService jobAdService;

    public JobAdRestController(JobAdService jobAdService) {
        this.jobAdService = jobAdService;
    }

    @GetMapping
    public ResponseEntity<List<JobAd>> getJobApplications() {

        List<JobAd> jobAds = jobAdService.getJobAds();

        return ResponseEntity.ok(jobAds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobAd> getJobAdById(@PathVariable int id) {
        try {
            JobAd jobAd = jobAdService.getJobAd(id);

            return ResponseEntity.ok(jobAd);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<JobAd> createJobApplication(@Valid @RequestBody JobAd jobAd) {

        jobAdService.addJobAd(jobAd);

        return ResponseEntity.ok(jobAd);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobAd> updateJobApplication(@PathVariable int id, @RequestBody JobAd jobAd) {
        jobAd.setId(id);
        jobAdService.updateJobAd(jobAd);
        return ResponseEntity.ok(jobAd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobApplication(@PathVariable int id) {
        jobAdService.removeJobAd(id);
        return ResponseEntity.ok("Job application has been deleted");
    }
}

package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.Exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.services.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationRestController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationRestController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getJobApplications() {

        List<JobApplication> jobApplications = jobApplicationService.getJobApplications();

        return ResponseEntity.ok(jobApplications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getJobApplicationById(@PathVariable int id) {
        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            return ResponseEntity.ok(jobApplication);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<JobApplication> createJobApplication(@Valid @RequestBody JobApplication jobApplication) {

        jobApplicationService.addJobApplication(jobApplication);

        return ResponseEntity.ok(jobApplication);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> updateJobApplication(@PathVariable int id, @RequestBody JobApplication jobApplication) {
        jobApplication.setId(id);
        jobApplicationService.updateJobApplication(jobApplication);
        return ResponseEntity.ok(jobApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobApplication(@PathVariable int id) {
        jobApplicationService.removeJobApplication(id);
        return ResponseEntity.ok("Job application has been deleted");
    }
}

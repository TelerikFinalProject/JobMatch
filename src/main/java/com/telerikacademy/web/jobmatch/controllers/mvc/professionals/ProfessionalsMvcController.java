package com.telerikacademy.web.jobmatch.controllers.mvc.professionals;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/professionals")
public class ProfessionalsMvcController {
    private final JobAdService jobAdService;

    @Autowired
    public ProfessionalsMvcController(JobAdService jobAdService) {
        this.jobAdService = jobAdService;
    }

    @GetMapping("/job-ads")
    public String getAllJobAds(Model model) {
        JobAdFilterOptions jobAdFilterOptions = new JobAdFilterOptions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        List<JobAd> jobAds = jobAdService.getJobAds(jobAdFilterOptions);
        model.addAttribute("jobAds", jobAds);
        return "job_listing";
    }

    @GetMapping("/job-ads/{id}")
    public String getJobAdById(Model model, @PathVariable int id) {
        try {
            JobAd jobAd = jobAdService.getJobAd(id);
            model.addAttribute("jobAd", jobAd);
            return "job_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "main";
        }
    }
}

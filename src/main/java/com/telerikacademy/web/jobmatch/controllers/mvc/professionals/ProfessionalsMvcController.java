package com.telerikacademy.web.jobmatch.controllers.mvc.professionals;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/professionals")
public class ProfessionalsMvcController {
    private final JobAdService jobAdService;
    private final LocationService locationService;

    @Autowired
    public ProfessionalsMvcController(JobAdService jobAdService,
                                      LocationService locationService) {
        this.jobAdService = jobAdService;
        this.locationService = locationService;
    }

    @GetMapping("/job-ads")
    public String getAllJobAds(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "7") int size,
                               @ModelAttribute("filterOptions") JobAdFilterOptions filterOptions,
                               Model model) {

        JobAdFilterOptions jobAdFilterOptions = new JobAdFilterOptions(Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());

        model.addAttribute("jobAdFilterOptions", jobAdFilterOptions);
        model.addAttribute("locations", locationService.getAllSavedLocations());

        List<JobAd> jobAds = jobAdService.getJobAds(jobAdFilterOptions);
        List<JobAd> paginatedJobAds = jobAdService.getPaginatedJobAds(jobAds, page, size);
        int totalPages = (int) Math.ceil((double) jobAds.size() / size);

        model.addAttribute("jobsSize", jobAds.size());
        model.addAttribute("jobAds", paginatedJobAds);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

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

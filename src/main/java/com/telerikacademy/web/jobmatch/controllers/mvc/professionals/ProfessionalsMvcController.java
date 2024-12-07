package com.telerikacademy.web.jobmatch.controllers.mvc.professionals;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.EmployersService;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.telerikacademy.web.jobmatch.helpers.FilterHelpers.jobAdMvcFilterHelper;

@Controller
@RequestMapping("/professionals")
public class ProfessionalsMvcController {
    private final JobAdService jobAdService;
    private final LocationService locationService;
    private final EmployersService employersService;

    @Autowired
    public ProfessionalsMvcController(JobAdService jobAdService,
                                      LocationService locationService,
                                      EmployersService employersService) {
        this.jobAdService = jobAdService;
        this.locationService = locationService;
        this.employersService = employersService;
    }

    @GetMapping("/job-ads")
    public String getAllJobAds(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "7") int size,
                               @ModelAttribute("filterOptions") JobAdFilterDto filterOptions,
                               Model model) {

        jobAdMvcFilterHelper(filterOptions);

        JobAdFilterOptions jobAdFilterOptions = new JobAdFilterOptions(
                null,
                filterOptions.getMinSalary(),
                filterOptions.getMaxSalary(),
                filterOptions.getLocation(),
                filterOptions.getCreator(),
                "Active",
                filterOptions.getHybrid());

        model.addAttribute("jobAdFilterOptions", filterOptions);
        model.addAttribute("locations", locationService.getAllSavedLocations());
        model.addAttribute("employers", employersService.getEmployers());

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

package com.telerikacademy.web.jobmatch.controllers.mvc.employers;

import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.JobApplicationService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.telerikacademy.web.jobmatch.helpers.FilterHelpers.jobApplicationMvcFilterHelper;

@Controller
@RequestMapping("/employers")
public class EmployersMvcController {
    private final LocationService locationService;
    private final JobApplicationService jobApplicationService;

    public EmployersMvcController(LocationService locationService,
                                  JobApplicationService jobApplicationService) {
        this.locationService = locationService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/job-applications")
    public String getAllJobApplications(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "7") int size,
                                        @ModelAttribute("filterOptions") JobApplicationFilterDto filterOptions,
                                        Model model) {

        jobApplicationMvcFilterHelper(filterOptions);

        JobApplicationFilterOptions jobApplicationFilterOptions = new JobApplicationFilterOptions(
                filterOptions.getMinSalary(),
                filterOptions.getMaxSalary(),
                null,
                filterOptions.getLocation(),
                "Active",
                filterOptions.getHybrid()
        );

        model.addAttribute("jobApplicationFilterOptions", filterOptions);
        model.addAttribute("locations", locationService.getAllSavedLocations());

        List<JobApplication> jobApplications = jobApplicationService.getJobApplications(jobApplicationFilterOptions);
        List<JobApplication> paginatedJobApplications = jobApplicationService
                .getPaginatedJobApplications(jobApplications, page, size);
        int totalPages = (int) Math.ceil((double) jobApplications.size() / size);

        model.addAttribute("applicationsSize", jobApplications.size());
        model.addAttribute("jobApplications", paginatedJobApplications);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "application_listing";
    }
}

package com.telerikacademy.web.jobmatch.controllers.mvc.employers;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.EmployersService;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import com.telerikacademy.web.jobmatch.services.contracts.JobApplicationService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.telerikacademy.web.jobmatch.helpers.FilterHelpers.jobAdMvcFilterHelper;
import static com.telerikacademy.web.jobmatch.helpers.FilterHelpers.jobApplicationMvcFilterHelper;

@Controller
@RequestMapping("/employers")
public class EmployersMvcController {
    private final LocationService locationService;
    private final JobApplicationService jobApplicationService;
    private final EmployersService employersService;
    private final JobAdService jobAdService;

    public EmployersMvcController(LocationService locationService,
                                  JobApplicationService jobApplicationService,
                                  EmployersService employersService,
                                  JobAdService jobAdService) {
        this.locationService = locationService;
        this.jobApplicationService = jobApplicationService;
        this.employersService = employersService;
        this.jobAdService = jobAdService;
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

    @GetMapping("/job-applications/{id}")
    public String getJobApplicationById(Model model, @PathVariable int id) {
        try {
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);
            model.addAttribute("jobApplication", jobApplication);
            return "application_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "main";
        }
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {

        //TODO Logged employer
        try {
            Employer loggedEmployer = employersService.getEmployer(37);
            model.addAttribute("employer", loggedEmployer);
            model.addAttribute("applicationsSize", jobApplicationService.getNumberOfActiveJobApplications());
            model.addAttribute("companyJobAdsSize", jobAdService.getJobAds(new JobAdFilterOptions(null, null,
                    null, null, loggedEmployer.getCompanyName(), null, null)).size());

            return "employers_dashboard";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "main";
        }
    }

    @GetMapping("/dashboard/job-ads")
    public String getJobAdsByCompany(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "7") int size,
                                     @ModelAttribute("filterOptions") JobAdFilterDto filterOptions,
                                     Model model) {

        try {
            //TODO get logged employer
            Employer loggedEmployer = employersService.getEmployer(33);
            jobAdMvcFilterHelper(filterOptions);
            JobAdFilterOptions jobAdFilterOptions = new JobAdFilterOptions(
                    null,
                    filterOptions.getMinSalary(),
                    filterOptions.getMaxSalary(),
                    filterOptions.getLocation(),
                    loggedEmployer.getCompanyName(),
                    filterOptions.getStatus(),
                    filterOptions.getHybrid());

            model.addAttribute("jobAdFilterOptions", filterOptions);
            model.addAttribute("employer", loggedEmployer);
            model.addAttribute("locations", locationService.getAllSavedLocations());

            List<JobAd> jobAds = jobAdService.getJobAds(jobAdFilterOptions);
            List<JobAd> paginatedJobAds = jobAdService.getPaginatedJobAds(jobAds, page, size);
            int totalPages = (int) Math.ceil((double) jobAds.size() / size);

            model.addAttribute("jobsSize", jobAds.size());
            model.addAttribute("jobAds", paginatedJobAds);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);


            return "company_job_ads_listing";

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "main";
        }
    }

    @GetMapping("/dashboard/job-ads/{id}")
    public String getJobAdByCompany(Model model, @PathVariable int id) {
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

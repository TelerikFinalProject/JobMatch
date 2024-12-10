package com.telerikacademy.web.jobmatch.controllers.mvc.employers;

import com.telerikacademy.web.jobmatch.exceptions.AuthenticationException;
import com.telerikacademy.web.jobmatch.exceptions.AuthorizationException;
import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.telerikacademy.web.jobmatch.helpers.FilterHelpers.jobAdMvcFilterHelper;
import static com.telerikacademy.web.jobmatch.helpers.FilterHelpers.jobApplicationMvcFilterHelper;

@Controller
@RequestMapping("/employers")
public class EmployersMvcController {
    private final LocationService locationService;
    private final JobApplicationService jobApplicationService;
    private final EmployersService employersService;
    private final JobAdService jobAdService;
    private final MatchService matchService;
    private final MailService mailService;

    public EmployersMvcController(LocationService locationService,
                                  JobApplicationService jobApplicationService,
                                  EmployersService employersService,
                                  JobAdService jobAdService,
                                  MatchService matchService,
                                  MailService mailService) {
        this.locationService = locationService;
        this.jobApplicationService = jobApplicationService;
        this.employersService = employersService;
        this.jobAdService = jobAdService;
        this.matchService = matchService;
        this.mailService = mailService;
    }

//    @ModelAttribute("userRole")
//    public String populateIsAuthenticated(HttpSession session) {
//        return session.getAttribute("userRole").toString();
//    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model,
                               HttpSession session) {

        try {
            rolesChecker(session);

            Employer loggedEmployer = employersService.getEmployer(session.getAttribute("currentUser").toString());

            model.addAttribute("employer", loggedEmployer);
            model.addAttribute("applicationsSize", jobApplicationService.getNumberOfActiveJobApplications());
            model.addAttribute("companyJobAdsSize", jobAdService.getJobAds(new JobAdFilterOptions(
                    null, null, null, null, loggedEmployer.getCompanyName(),
                    null, null)).size());

            return "employers_dashboard";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-applications")
    public String getAllJobApplications(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "7") int size,
                                        @ModelAttribute("filterOptions") JobApplicationFilterDto filterOptions,
                                        Model model,
                                        HttpSession session) {

        try {
            rolesChecker(session);
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }

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

    @GetMapping("/dashboard/job-applications/{id}")
    public String getJobApplicationById(Model model,
                                        @PathVariable int id,
                                        HttpSession session) {
        try {
            rolesChecker(session);
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);
            model.addAttribute("jobApplication", jobApplication);
            return "application_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-ads")
    public String getJobAdsByCompany(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "7") int size,
                                     @ModelAttribute("filterOptions") JobAdFilterDto filterOptions,
                                     Model model,
                                     HttpSession session) {

        try {
            rolesChecker(session);
            Employer loggedEmployer = employersService.getEmployer(session.getAttribute("currentUser").toString());
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
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-ads/{id}")
    public String getJobAdByCompany(Model model,
                                    @PathVariable int id,
                                    HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAd = jobAdService.getJobAd(id);

            checkAdCreator(session, jobAd);

            model.addAttribute("jobAd", jobAd);
            return "job_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-ads/{id}/delete")
    public String deleteJobAd(Model model,
                                  @PathVariable int id,
                                  HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAd = jobAdService.getJobAd(id);

            checkAdCreator(session, jobAd);

            jobAdService.removeJobAd(id);

            return "redirect:/employers/dashboard/job-ads";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-ads/{id}/update")
    public String showUpdateJobAdView(Model model,
                              @PathVariable int id,
                              HttpSession session) {
        return null;
    }

    @PostMapping("/dashboard/job-ads/{id}/update")
    public String updateJobAd(Model model,
                                  @PathVariable int id,
                                  HttpSession session) {
        return null;
    }

    @GetMapping("/dashboard/job-ads/{id}/find-matches")
    public String getAllMatchedJobApplicationsByJobAd(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "7") int size,
                                                      Model model,
                                                      @PathVariable int id,
                                                      HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAd = jobAdService.getJobAd(id);

            checkAdCreator(session, jobAd);

            model.addAttribute("jobAd", jobAd);
            Set<JobApplication> matches = matchService.getSuitableApplications(jobAd);

            List<JobApplication> paginatedJobApplications = jobApplicationService
                    .getPaginatedJobApplications(matches.stream().toList(), page, size);
            int totalPages = (int) Math.ceil((double) matches.size() / size);

            model.addAttribute("jobApplications", paginatedJobApplications);
            model.addAttribute("applicationsSize", matches.size());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);


            return "matched_applications";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-ads/{jobAdId}/find-matches/job-applications/{jobApplicationId}")
    public String getMatchedJobApplicationByJobAd(Model model,
                                                  @PathVariable int jobAdId,
                                                  @PathVariable int jobApplicationId,
                                                  HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAdToMatchWith = jobAdService.getJobAd(jobAdId);

            checkAdCreator(session, jobAdToMatchWith);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);
            model.addAttribute("jobAdId", jobAdId);
            model.addAttribute("jobApplicationId", jobApplicationId);

            model.addAttribute("jobApplication", jobApplicationService.getJobApplication(jobApplicationId));
            model.addAttribute("isApplicationSuitable",
                    matchService.getSuitableApplications(jobAdToMatchWith).contains(jobApplication));
            model.addAttribute("isAlreadyRequestedByAd",
                    jobAdToMatchWith.getMatchesSentToJobApplications().contains(jobApplication));
            model.addAttribute("isAlreadyRequestedByApplication",
                    jobAdToMatchWith.getMatchRequestedApplications().contains(jobApplication));

            return "application_match-requests";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-ads/{jobAdId}/find-matches/job-applications/{jobApplicationId}/request-match")
    public String requestMatchWithJobApplication(Model model,
                                                 @PathVariable int jobAdId,
                                                 @PathVariable int jobApplicationId,
                                                 HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAdToMatchWith = jobAdService.getJobAd(jobAdId);

            checkAdCreator(session, jobAdToMatchWith);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            if (!matchService.getSuitableApplications(jobAdToMatchWith).contains(jobApplication)) {
                throw new EntityNotFoundException(String.
                        format("Job %s with ID:%d is not suitable for your %s!", "application", jobApplicationId, "ad"));
            }

            if (jobAdToMatchWith.getMatchRequestedApplications().contains(jobApplication)) {
                throw new EntityDuplicateException(String.format("A match request for Job %s with ID:%d already exists, " +
                        "please reach out to the Job %s creator!", "application", jobAdId, "application"));
            }

            if (!jobAdToMatchWith.getMatchesSentToJobApplications().add(jobApplication)) {
                throw new EntityDuplicateException(String.format("A match request for Job %s with ID:%d has already been" +
                        " initiated!", "application", jobApplicationId));
            }

            mailService.sendNotificationViaEmailToApplicant(jobApplication, jobAdToMatchWith);
            jobAdService.updateJobAd(jobAdToMatchWith);
            return String.format("redirect:/employers/dashboard/job-ads/%d/find-matches/job-applications/%d",
                    jobAdId, jobApplicationId);

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (EntityDuplicateException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-ads/{jobAdId}/find-matches/job-applications/{jobApplicationId}/approve-match")
    public String approveMatchWithJobApplication(Model model,
                                                 @PathVariable int jobAdId,
                                                 @PathVariable int jobApplicationId,
                                                 HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAdToMatchWith = jobAdService.getJobAd(jobAdId);

            checkAdCreator(session, jobAdToMatchWith);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            matchService.approveJobApplication(jobAdToMatchWith, jobApplication);

            return String.format("redirect:/employers/dashboard/job-ads/%d",
                    jobAdId);

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-ads/{jobAdId}/find-matches/job-applications/{jobApplicationId}/decline-match")
    public String declineMatchWithJobApplication(Model model,
                                                 @PathVariable int jobAdId,
                                                 @PathVariable int jobApplicationId,
                                                 HttpSession session) {
        try {
            rolesChecker(session);

            JobAd jobAdToMatchWith = jobAdService.getJobAd(jobAdId);

            checkAdCreator(session, jobAdToMatchWith);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            matchService.declineJobApplication(jobAdToMatchWith, jobApplication);

            return String.format("redirect:/employers/dashboard/job-ads/%d/find-matches/job-applications/%d",
                    jobAdId, jobApplicationId);

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }



    private static void rolesChecker(HttpSession session) {
        if (session.getAttribute("userRole") == null){
            throw new AuthenticationException("Not authenticated");
        }

        String role = session.getAttribute("userRole").toString();

        if (!role.equals("ADMIN") && !role.equals("EMPLOYER")) {
            throw new AuthorizationException("access", "resource");
        }
    }

    private static void checkAdCreator(HttpSession session, JobAd jobAd) {
        if (session.getAttribute("currentUser") == null){
            throw new AuthenticationException("Not authenticated");
        }

        String user = session.getAttribute("currentUser").toString();

        if (!jobAd.getEmployer().getUsername().equals(user)) {
            throw new AuthorizationException("access", "job ad");
        }
    }
}

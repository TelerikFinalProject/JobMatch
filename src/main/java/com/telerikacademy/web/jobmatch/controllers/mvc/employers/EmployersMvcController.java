package com.telerikacademy.web.jobmatch.controllers.mvc.employers;

import com.telerikacademy.web.jobmatch.exceptions.AuthenticationException;
import com.telerikacademy.web.jobmatch.exceptions.AuthorizationException;
import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.JobAdMappers;
import com.telerikacademy.web.jobmatch.helpers.EmployerMappers;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.models.dtos.JobAdDtoIn;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.EmployerDetailsDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.PasswordChangeDto;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private final StatusService statusService;
    private final SkillService skillService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public EmployersMvcController(LocationService locationService,
                                  JobApplicationService jobApplicationService,
                                  EmployersService employersService,
                                  JobAdService jobAdService,
                                  MatchService matchService,
                                  MailService mailService,
                                  UserService userService,
                                  PasswordEncoder passwordEncoder,
                                  SkillService skillService,
                                  StatusService statusService) {
        this.locationService = locationService;
        this.jobApplicationService = jobApplicationService;
        this.employersService = employersService;
        this.jobAdService = jobAdService;
        this.matchService = matchService;
        this.mailService = mailService;
        this.skillService = skillService;
        this.statusService = statusService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute("userRole")
    public String populateIsAuthenticated(HttpSession session) {
        if (session.getAttribute("userRole") == null) {
            return "Unauthenticated";
        }
        return session.getAttribute("userRole").toString();
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

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
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/{id}")
    public String getEmployerDetails(@PathVariable int id,
                                    Model model,
                                    HttpSession session) {

        try {
            checkIfAuthenticated(session);
            Employer employer = employersService.getEmployer(id);

            if (session.getAttribute("userRole").equals("EMPLOYER")) {
                checkIfLoggedUserIsOwner(session, employer);
            }

            List<JobAd> jobAds = jobAdService.getJobAds(new JobAdFilterOptions(null, null, null, null, employer.getCompanyName(), null, null));
            model.addAttribute("jobAds", jobAds);
            model.addAttribute("employer", employer);
            return "company-details";

        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/{id}/change-password")
    public String getUpdateEmployerPassword(@PathVariable int id,
                                            Model model,
                                            HttpSession session) {

        try {
            rolesChecker(session);
            Employer employer = employersService.getEmployer(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, employer);

            model.addAttribute("passwordChangeDto", new PasswordChangeDto());
            return "change-password";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/{id}/change-password")
    public String handleUpdateEmployerPassword(@PathVariable int id,
                                               @Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto passwordChangeDto,
                                               BindingResult bindingResult,
                                               HttpSession session,
                                               Model model) {

        try {
            rolesChecker(session);
            UserPrincipal employer = employersService.getEmployer(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, employer);

            if (!passwordEncoder.matches(employer.getPassword(), passwordChangeDto.getPreviousPassword())) {
                bindingResult.rejectValue("previousPassword", "Please provide your current password");
            }

            if (bindingResult.hasErrors()) {
                return "change-password";
            }

            userService.changePassword(employer, passwordChangeDto);

            return "redirect:/employers/" + id;
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/{id}/change-details")
    public String getUpdateEmployerDetails(@PathVariable int id,
                                          Model model,
                                          HttpSession session) {
        try {
            rolesChecker(session);
            Employer employer = employersService.getEmployer(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, employer);

            EmployerDetailsDto employerDetailsDto = EmployerMappers.INSTANCE.toEmployerDetailsDto(employer);
            List<JobAd> jobAds = jobAdService.getJobAds(new JobAdFilterOptions(null, null, null, null, employer.getCompanyName(), null, null));

            model.addAttribute("jobAds", jobAds);
            model.addAttribute("employer", employer);
            model.addAttribute("employerDetails", employerDetailsDto);
            model.addAttribute("countries", locationService.getAllCountries());
            model.addAttribute("currentCountry", locationService.getCountryByIsoCode(employer.getLocation().getIsoCode()).getName());
            return "company-details-update";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/{id}/change-details")
    public String handleUpdateEmployerDetails(@PathVariable int id,
                                              @Valid @ModelAttribute("employerDetails") EmployerDetailsDto employerDetailsDto,
                                              BindingResult bindingResult,
                                              Model model,
                                              HttpSession session) {

        try {
            rolesChecker(session);

            Employer employer = employersService.getEmployer(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, employer);

            List<JobAd> jobAds = jobAdService.getJobAds(new JobAdFilterOptions(null, null, null, null, employer.getCompanyName(), null, null));

            if (bindingResult.hasErrors()) {
                model.addAttribute("jobAds", jobAds);
                model.addAttribute("employer", employer);
                return "company-details-update";
            }

            Employer updatedEmployer = EmployerMappers.INSTANCE.fromEmployerDetailsDto(employerDetailsDto, employer, locationService);

            employersService.updateEmployer(updatedEmployer);

            return "redirect:/employers/" + id;
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/skills")
    public String getDashboardSkillsView(HttpSession session,
                                         Model model) {
        try {
            rolesChecker(session);
            model.addAttribute("skills", jobApplicationService.getAllUniqueSkillsUsedInJobApplications());
            return "employer-skills-dashboard";

        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        }
    }

    @GetMapping("/dashboard/skills/{id}")
    public String getJobApplicationsBySkill(@PathVariable int id,
                                            HttpSession session,
                                            Model model) {
        try {
            rolesChecker(session);
            model.addAttribute("jobApplications", jobApplicationService.getAllBySkill(skillService.getSkill(id)));
            model.addAttribute("skill", skillService.getSkill(id));
            return "job-applications-by-skill";

        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
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
        } catch (AuthenticationException e) {
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
            model.addAttribute("currentUser", session.getAttribute("currentUser").toString());

            return "application_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
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
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-ads/create")
    public String showCreateJobAdView(Model model,
                                      HttpSession session) {
        try {
            rolesChecker(session);

            model.addAttribute("ad", new JobAdDtoIn());
            model.addAttribute("countries", locationService.getAllCountries());
            model.addAttribute("AllSkills", skillService.getAllSkills());

            return "job_ad_create_form";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-ads/create")
    public String createJobAd(@Valid @ModelAttribute("ad") JobAdDtoIn adDtoIn,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("AllSkills", skillService.getAllSkills());
            model.addAttribute("countries", locationService.getAllCountries());
            return "job_ad_create_form";
        }

        Employer employer;
        try {
            rolesChecker(session);
            employer = employersService.getEmployer(session.getAttribute("currentUser").toString());

            if (adDtoIn.getLocCountryIsoCode() == null || adDtoIn.getLocCityId() == null) {
                adDtoIn.setLocCountryIsoCode("Hm");
                adDtoIn.setLocCityId(1);
            }

            JobAd jobAd = JobAdMappers.INSTANCE.fromDtoIn(adDtoIn, locationService, statusService, skillService);

            jobAd.setEmployer(employer);

        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }

        return "redirect:/employers/dashboard";
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
            model.addAttribute("currentUser", session.getAttribute("currentUser").toString());

            return "job_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
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
        } catch (AuthenticationException e) {
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
        } catch (AuthenticationException e) {
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
            model.addAttribute("isAlreadyRequestedByApplication",
                    jobApplication.getMatchesSentToJobAds().contains(jobAdToMatchWith));
            model.addAttribute("isAlreadyRequestedByAd",
                    jobAdToMatchWith.getMatchesSentToJobApplications().contains(jobApplication));

            return "application_match-requests";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
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
                        "please reach out to the Job %s creator!", "application", jobApplicationId, "application"));
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
        } catch (AuthenticationException e) {
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
        } catch (AuthenticationException e) {
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
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }


    private static void rolesChecker(HttpSession session) {
        if (session.getAttribute("userRole") == null) {
            throw new AuthenticationException("Not authenticated");
        }

        String role = session.getAttribute("userRole").toString();

        if (!role.equals("ADMIN") && !role.equals("EMPLOYER")) {
            throw new AuthorizationException("access", "resource");
        }
    }

    private static void checkAdCreator(HttpSession session, JobAd jobAd) {
        if (session.getAttribute("currentUser") == null) {
            throw new AuthenticationException("Not authenticated");
        }

        String user = session.getAttribute("currentUser").toString();

        if (!jobAd.getEmployer().getUsername().equals(user)) {
            throw new AuthorizationException("access", "job ad");
        }
    }

    private static boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userRole") != null;
    }

    private void checkIfAuthenticated(HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new AuthenticationException("Not authenticated");
        }
    }

    private void checkIfLoggedUserIsOwner(HttpSession session, UserPrincipal user) {
        if (!session.getAttribute("currentUser").equals(user.getUsername())){
            throw new AuthorizationException("access", "dashboard");
        }
    }
}

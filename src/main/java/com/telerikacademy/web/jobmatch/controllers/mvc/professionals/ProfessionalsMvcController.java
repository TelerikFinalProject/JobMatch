package com.telerikacademy.web.jobmatch.controllers.mvc.professionals;

import com.telerikacademy.web.jobmatch.exceptions.AuthenticationException;
import com.telerikacademy.web.jobmatch.exceptions.AuthorizationException;
import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.JobApplicationMappers;
import com.telerikacademy.web.jobmatch.helpers.ProfessionalMappers;
import com.telerikacademy.web.jobmatch.models.*;
import com.telerikacademy.web.jobmatch.models.dtos.JobApplicationDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobAdFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.filters.JobApplicationFilterDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.EmployerDetailsDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.PasswordChangeDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.mvc.ProfessionalDetailsDto;
import com.telerikacademy.web.jobmatch.models.filter_options.JobAdFilterOptions;
import com.telerikacademy.web.jobmatch.models.filter_options.JobApplicationFilterOptions;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/professionals")
public class ProfessionalsMvcController {
    private final JobAdService jobAdService;
    private final JobApplicationService jobApplicationService;
    private final LocationService locationService;
    private final EmployersService employersService;
    private final ProfessionalService professionalService;
    private final MatchService matchService;
    private final MailService mailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SkillService skillService;
    private final StatusService statusService;

    @Autowired
    public ProfessionalsMvcController(JobAdService jobAdService,
                                      LocationService locationService,
                                      EmployersService employersService,
                                      ProfessionalService professionalService,
                                      JobApplicationService jobApplicationService,
                                      MatchService matchService,
                                      MailService mailService,
                                      UserService userService,
                                      PasswordEncoder passwordEncoder,
                                      SkillService skillService,
                                      StatusService statusService) {
        this.jobAdService = jobAdService;
        this.locationService = locationService;
        this.employersService = employersService;
        this.professionalService = professionalService;
        this.jobApplicationService = jobApplicationService;
        this.matchService = matchService;
        this.mailService = mailService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.skillService = skillService;
        this.statusService = statusService;
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

            Professional loggedProfessional = professionalService.getProfessionalByUsername
                    (session.getAttribute("currentUser").toString());

            model.addAttribute("professional", loggedProfessional);
            model.addAttribute("applicationsSize", jobApplicationService.getJobApplications
                    (new JobApplicationFilterOptions(null, null, loggedProfessional.getUsername(),
                            null, null, null)).size());
            model.addAttribute("companyJobAdsSize", jobAdService.getNumberOfActiveJobAds());
            model.addAttribute("featuredJobs", jobAdService.getFeaturedAds());

            return "professionals_dashboard";
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
    public String getProfessionalDetails(@PathVariable int id,
                                     Model model,
                                     HttpSession session) {

        try {
            checkIfAuthenticated(session);
            Professional professional = professionalService.getProfessional(id);

            if (session.getAttribute("userRole").equals("PROFESSIONAL")) {
                checkIfLoggedUserIsOwner(session, professional);
            }

            List<JobApplication> jobApplications = jobApplicationService.getJobApplications(new JobApplicationFilterOptions(null, null, professional.getUsername(), null, null, null));
            model.addAttribute("jobApplications", jobApplications);
            model.addAttribute("professional", professional);
            return "professional-details";

        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/{id}/change-password")
    public String getUpdateProfessionalPassword(@PathVariable int id,
                                                Model model,
                                                HttpSession session) {

        try {
            rolesChecker(session);
            Professional professional = professionalService.getProfessionalByUsername(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, professional);

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
    public String handleUpdateProfessionalPassword(@PathVariable int id,
                                               @Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto passwordChangeDto,
                                               BindingResult bindingResult,
                                               HttpSession session,
                                               Model model) {

        try {
            rolesChecker(session);
            UserPrincipal professional = professionalService.getProfessionalByUsername(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, professional);

            if (passwordEncoder.matches(passwordChangeDto.getPassword(), professional.getPassword())) {
                bindingResult.rejectValue("previousPassword", "passwords.match", "Please provide your current password");
            }

            if (bindingResult.hasErrors()) {
                return "change-password";
            }

            userService.changePassword(professional, passwordChangeDto);

            return "redirect:/professionals/" + id;
        } catch (AuthenticationException | AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        }
    }

    @GetMapping("/{id}/change-details")
    public String getUpdateProfessionalDetails(@PathVariable int id,
                                           Model model,
                                           HttpSession session) {
        try {
            rolesChecker(session);
            Professional professional = professionalService.getProfessionalByUsername(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, professional);

            ProfessionalDetailsDto professionalDetailsDto = ProfessionalMappers.INSTANCE.toProfessionalDetailsDto(professional);
            List<JobApplication> jobApplications = jobApplicationService.getJobApplications(new JobApplicationFilterOptions(null, null, professional.getUsername(), null, null, null));

            model.addAttribute("jobApplications", jobApplications);
            model.addAttribute("professional", professional);
            model.addAttribute("professionalDetails", professionalDetailsDto);
            model.addAttribute("countries", locationService.getAllCountries());
            model.addAttribute("currentCountry", locationService.getCountryByIsoCode(professional.getLocation().getIsoCode()).getName());
            return "professional-details-update";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/{id}/change-details")
    public String handleUpdateProfessionalDetails(@PathVariable int id,
                                              @Valid @ModelAttribute("professionalDetails")
                                              ProfessionalDetailsDto professionalDetailsDto,
                                              BindingResult bindingResult,
                                              Model model,
                                              HttpSession session) {

        try {
            rolesChecker(session);

            Professional professional = professionalService.getProfessionalByUsername(session.getAttribute("currentUser").toString());

            checkIfLoggedUserIsOwner(session, professional);

            List<JobApplication> jobApplications = jobApplicationService.getJobApplications(new JobApplicationFilterOptions(null, null, professional.getUsername(), null, null, null));

            if (bindingResult.hasErrors()) {
                model.addAttribute("jobApplications", jobApplications);
                model.addAttribute("professional", professional);
                return "professional-details-update";
            }

            Professional updatedProfessional = ProfessionalMappers.INSTANCE.fromProfessionalDetailsDto(professionalDetailsDto, professional, locationService);

            professionalService.updateProfessional(updatedProfessional);

            return "redirect:/professionals/" + id;
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
            model.addAttribute("skills", jobAdService.getAllUniqueSkillsUsedInJobAds());
            return "professional-skills-dashboard";

        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        }
    }

    @GetMapping("/dashboard/job-ads")
    public String getAllJobAds(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "7") int size,
                               @ModelAttribute("filterOptions") JobAdFilterDto filterOptions,
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


    @GetMapping("/dashboard/job-ads/{id}")
    public String getJobAdById(Model model,
                               @PathVariable int id,
                               HttpSession session) {
        try {
            rolesChecker(session);
            JobAd jobAd = jobAdService.getJobAd(id);

            model.addAttribute("jobAd", jobAd);
            model.addAttribute("currentUser", session.getAttribute("currentUser").toString());

            return "job_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "main";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @GetMapping("/dashboard/job-applications")
    public String getJobApplicationsByPro(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "7") int size,
                                          @ModelAttribute("filterOptions") JobApplicationFilterDto filterOptions,
                                          Model model,
                                          HttpSession session){
        try {
            rolesChecker(session);
            Professional loggedProfessional = professionalService
                    .getProfessionalByUsername(session.getAttribute("currentUser").toString());
            jobApplicationMvcFilterHelper(filterOptions);
            JobApplicationFilterOptions jobApplicationFilterOptions = new JobApplicationFilterOptions(
                    filterOptions.getMinSalary(),
                    filterOptions.getMaxSalary(),
                    loggedProfessional.getUsername(),
                    filterOptions.getLocation(),
                    filterOptions.getStatus(),
                    filterOptions.getHybrid());

            model.addAttribute("jobApplicationFilterOptions", filterOptions);
            model.addAttribute("professional", loggedProfessional);
            model.addAttribute("locations", locationService.getAllSavedLocations());

            List<JobApplication> jobApplications = jobApplicationService.getJobApplications(jobApplicationFilterOptions);
            List<JobApplication> paginatedJobApplications = jobApplicationService
                    .getPaginatedJobApplications(jobApplications, page, size);

            int totalPages = (int) Math.ceil((double) jobApplications.size() / size);

            model.addAttribute("jobsSize", jobApplications.size());
            model.addAttribute("jobApplications", paginatedJobApplications);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);


            return "professional_view_application_listing";

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

    @GetMapping("/dashboard/job-applications/create")
    public String showCreateJobAdView(Model model,
                                      HttpSession session) {
        try {
            rolesChecker(session);

            model.addAttribute("application", new JobApplicationDtoIn());
            model.addAttribute("countries", locationService.getAllCountries());
            model.addAttribute("AllSkills", skillService.getAllSkills());

            return "job_application_create_form";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-applications/create")
    public String createJobAd(@Valid @ModelAttribute("application") JobApplicationDtoIn applicationDtoIn,
                              BindingResult bindingResult,
                              Model model,
                              HttpSession session) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("AllSkills", skillService.getAllSkills());
            model.addAttribute("countries", locationService.getAllCountries());
            return "job_application_create_form";
        }

        Professional professional;
        try {
            rolesChecker(session);
            professional = professionalService.getProfessionalByUsername(session.getAttribute("currentUser").toString());

            if (applicationDtoIn.getLocCountryIsoCode() == null || applicationDtoIn.getLocCityId() == null) {
                applicationDtoIn.setLocCountryIsoCode("Hm");
                applicationDtoIn.setLocCityId(1);
            }

            JobApplication jobApplication = JobApplicationMappers.INSTANCE
                    .fromDtoIn(applicationDtoIn, statusService, locationService, skillService);

            jobApplication.setProfessional(professional);

        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error-view";
        } catch (AuthenticationException e) {
            return "redirect:/authentication/login";
        }

        return "redirect:/professionals/dashboard";
    }

    @GetMapping("/dashboard/job-applications/{id}")
    public String getJobApplicationById(Model model,
                                        HttpSession session,
                                        @PathVariable int id) {
        try {
            rolesChecker(session);
            JobApplication jobApplication = jobApplicationService.getJobApplication(id);
            checkApplicationCreator(session, jobApplication);

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
        } catch (AuthenticationException e){
            return "redirect:/authentication/login";
        }
    }

    @PostMapping("/dashboard/job-applications/{id}/delete")
    public String deleteJobApplication(Model model,
                              @PathVariable int id,
                              HttpSession session) {
        try {
            rolesChecker(session);

            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            checkApplicationCreator(session, jobApplication);

            jobApplicationService.removeJobApplication(id);

            return "redirect:/professionals/dashboard/job-applications";
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

    @GetMapping("/dashboard/job-applications/{id}/update")
    public String showUpdateJobApplicationView(Model model,
                                      @PathVariable int id,
                                      HttpSession session) {
        return null;
    }

    @PostMapping("/dashboard/job-applications/{id}/update")
    public String updateJobApplication(Model model,
                              @PathVariable int id,
                              HttpSession session) {
        return null;
    }


    @GetMapping("/dashboard/job-applications/{id}/find-matches")
    public String getAllMatchedJobAdsByJobApplication(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "7") int size,
                                                      Model model,
                                                      @PathVariable int id,
                                                      HttpSession session){
        try {
            rolesChecker(session);

            JobApplication jobApplication = jobApplicationService.getJobApplication(id);

            checkApplicationCreator(session, jobApplication);

            model.addAttribute("jobApplication", jobApplication);
            Set<JobAd> matches = matchService.getSuitableAds(jobApplication);

            List<JobAd> paginatedJobAds = jobAdService
                    .getPaginatedJobAds(matches.stream().toList(), page, size);

            int totalPages = (int) Math.ceil((double) matches.size() / size);

            model.addAttribute("jobAds", paginatedJobAds);
            model.addAttribute("adsSize", matches.size());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);

            return "matched_ads";
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

    @GetMapping("/dashboard/job-applications/{jobApplicationId}/find-matches/job-ads/{jobAdId}")
    public String getMatchedJobAdsByJobApplication(Model model,
                                                   @PathVariable int jobAdId,
                                                   @PathVariable int jobApplicationId,
                                                   HttpSession session){
        try {
            rolesChecker(session);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            checkApplicationCreator(session, jobApplication);

            JobAd jobAd = jobAdService.getJobAd(jobAdId);
            model.addAttribute("jobAdId", jobAdId);
            model.addAttribute("jobApplicationId", jobApplicationId);

            model.addAttribute("jobAd", jobAd);
            model.addAttribute("isAdSuitable",
                    matchService.getSuitableAds(jobApplication).contains(jobAd));
            model.addAttribute("isAlreadyRequestedByApplication",
                    jobApplication.getMatchesSentToJobAds().contains(jobAd));
            model.addAttribute("isAlreadyRequestedByAd",
                    jobAd.getMatchesSentToJobApplications().contains(jobApplication));

            return "ad_match-requests";
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

    @PostMapping("/dashboard/job-applications/{jobApplicationId}/find-matches/job-ads/{jobAdId}/request-match")
    public String requestMatchWithJobApplication(Model model,
                                                 @PathVariable int jobAdId,
                                                 @PathVariable int jobApplicationId,
                                                 HttpSession session) {
        try {
            rolesChecker(session);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            checkApplicationCreator(session, jobApplication);

            JobAd jobAd = jobAdService.getJobAd(jobAdId);

            if (!matchService.getSuitableAds(jobApplication).contains(jobAd)) {
                throw new EntityNotFoundException(String.
                        format("Job %s with ID:%d is not suitable for your %s!", "ad", jobApplicationId, "application"));
            }

            if (jobApplication.getMatchRequestedAds().contains(jobAd)) {
                throw new EntityDuplicateException(String.format("A match request for Job %s with ID:%d already exists, " +
                        "please reach out to the Job %s creator!", "ad", jobAdId, "ad"));
            }

            if (!jobApplication.getMatchesSentToJobAds().add(jobAd)) {
                throw new EntityDuplicateException(String.format("A match request for Job %s with ID:%d has already been" +
                        " initiated!", "ad", jobAdId));
            }

            mailService.sendNotificationViaEmailToEmployer(jobApplication, jobAd);
            jobApplicationService.updateJobApplication(jobApplication);
            return String.format("redirect:/professionals/dashboard/job-applications/%s/find-matches/job-ads/%s",
                    jobApplicationId, jobAdId);

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

    @PostMapping("/dashboard/job-applications/{jobApplicationId}/find-matches/job-ads/{jobAdId}/approve-match")
    public String approveMatchWithJobAd(Model model,
                                                 @PathVariable int jobAdId,
                                                 @PathVariable int jobApplicationId,
                                                 HttpSession session) {
        try {
            rolesChecker(session);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            checkApplicationCreator(session, jobApplication);

            JobAd jobAd = jobAdService.getJobAd(jobAdId);

            matchService.approveJobAd(jobAd, jobApplication);

            return String.format("redirect:/professionals/dashboard/job-applications/%d", jobApplicationId);

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

    @PostMapping("/dashboard/job-applications/{jobApplicationId}/find-matches/job-ads/{jobAdId}/decline-match")
    public String declineMatchWithJobAd(Model model,
                                                 @PathVariable int jobAdId,
                                                 @PathVariable int jobApplicationId,
                                                 HttpSession session) {
        try {
            rolesChecker(session);

            JobApplication jobApplication = jobApplicationService.getJobApplication(jobApplicationId);

            checkApplicationCreator(session, jobApplication);

            JobAd jobAd = jobAdService.getJobAd(jobAdId);

            matchService.declineAd(jobAd, jobApplication);

            return String.format("redirect:/professionals/dashboard/job-applications/%s/find-matches/job-ads/%s",
                    jobApplicationId, jobAdId);

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

        if (!role.equals("ADMIN") && !role.equals("PROFESSIONAL")) {
            throw new AuthorizationException("access", "resource");
        }
    }

    private static void checkApplicationCreator(HttpSession session, JobApplication jobApplication) {
        if (session.getAttribute("currentUser") == null) {
            throw new AuthenticationException("Not authenticated");
        }

        String user = session.getAttribute("currentUser").toString();

        if (!jobApplication.getProfessional().getUsername().equals(user)) {
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

package com.telerikacademy.web.jobmatch.controllers.mvc.publics;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.EmployerMappers;
import com.telerikacademy.web.jobmatch.helpers.UserMappers;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.LoginDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.users.UserDtoIn;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import com.telerikacademy.web.jobmatch.services.security.JwtTokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationMvcController {

    public static final String USERNAME_IS_ALREADY_TAKEN = "Username is already taken";
    public static final String EMAIL_IS_ALREADY_TAKEN = "Email is already taken";
    public static final String COMPANY_NAME_IS_ALREADY_TAKEN = "Company name is already taken";
    private final LocationService locationService;
    private final UserService userService;
    private final EmployersService employersService;
    private final AuthenticationService authenticationService;
    private final ProfessionalService professionalService;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/professional/register")
    public String showProfessionalRegister(Model model,
                                           HttpSession session) {

        UserDtoIn sessionUser = (UserDtoIn) session.getAttribute("userDtoIn");

        if (sessionUser == null) {
            sessionUser = new UserDtoIn();
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("countries", locationService.getAllCountries());
        return "user-register";
    }

    @PostMapping("/professional/register")
    public String handleFirstProfessionalRegister(@Valid @ModelAttribute("user") UserDtoIn userDtoIn,
                                                  BindingResult bindingResult,
                                                  Model model,
                                                  HttpSession session) {

        try {
            userService.findByUsername(userDtoIn.getUsername());
            bindingResult.rejectValue("username", "username.exists", USERNAME_IS_ALREADY_TAKEN);
        } catch (EntityNotFoundException ignored) {}

        try {
            userService.findByEmail(userDtoIn.getEmail());
            bindingResult.rejectValue("email", "email.exists", EMAIL_IS_ALREADY_TAKEN);
        } catch (EntityNotFoundException ignored) {}

        if (bindingResult.hasErrors()) {
            model.addAttribute("countries", locationService.getAllCountries());
            return "user-register";
        }

        ProfessionalDtoIn professionalDtoIn = new ProfessionalDtoIn();
        professionalDtoIn.setUsername(userDtoIn.getUsername());
        professionalDtoIn.setEmail(userDtoIn.getEmail());
        professionalDtoIn.setPassword(userDtoIn.getPassword());
        professionalDtoIn.setConfirmPassword(userDtoIn.getConfirmPassword());
        professionalDtoIn.setLocCountryIsoCode(userDtoIn.getLocCountryIsoCode());
        professionalDtoIn.setLocCityId(userDtoIn.getLocCityId());

        session.setAttribute("professionalFromSession", professionalDtoIn);
        model.addAttribute("professional", professionalDtoIn);
        session.setAttribute("userDtoIn", userDtoIn);
        return "professional-register";
    }

    @PostMapping("/professional/register/submit")
    public String handleProfessionalRegister(@ModelAttribute("professional") ProfessionalDtoIn professionalDtoIn,
                                             BindingResult bindingResult,
                                             HttpSession session) {

        ProfessionalDtoIn professionalFromSession = (ProfessionalDtoIn) session.getAttribute("professionalFromSession");

        if (professionalFromSession == null) {
            return "error-view";
        } else {
            professionalDtoIn.setUsername(professionalFromSession.getUsername());
            professionalDtoIn.setPassword(professionalFromSession.getPassword());
            professionalDtoIn.setConfirmPassword(professionalFromSession.getConfirmPassword());
            professionalDtoIn.setEmail(professionalFromSession.getEmail());
            professionalDtoIn.setLocCountryIsoCode(professionalFromSession.getLocCountryIsoCode());
            professionalDtoIn.setLocCityId(professionalFromSession.getLocCityId());
        }

        if (bindingResult.hasErrors()) {
            return "professional-register";
        }

        professionalService.registerProfessional(professionalDtoIn);
        Professional professional = professionalService.getProfessionalByUsername(professionalDtoIn.getUsername());
        session.setAttribute("currentUser", professional.getUsername());
        session.setAttribute("userRole", professional.getRoles());
        session.removeAttribute("professionalFromSession");
        session.removeAttribute("userDtoIn");

        return "redirect:/professionals/job-ads";
    }

    @GetMapping("/employer/register")
    public String showEmployerRegister(Model model,
                                       HttpSession session) {
        UserDtoIn sessionUser = (UserDtoIn) session.getAttribute("userDtoIn");

        if (sessionUser == null) {
            sessionUser = new UserDtoIn();
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("countries", locationService.getAllCountries());
        return "user-register";
    }

    @PostMapping("/employer/register")
    public String handleFirstEmployerRegister(@Valid @ModelAttribute("user") UserDtoIn userDtoIn,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpSession session) {

        try {
            userService.findByUsername(userDtoIn.getUsername());
            bindingResult.rejectValue("username", "username.exists", USERNAME_IS_ALREADY_TAKEN);
        } catch (EntityNotFoundException ignored) {}
        // check for existing email
        try {
            userService.findByEmail(userDtoIn.getEmail());
            bindingResult.rejectValue("email", "email.exists", EMAIL_IS_ALREADY_TAKEN);
        } catch (EntityNotFoundException ignored) {}

        if (bindingResult.hasErrors()) {
            model.addAttribute("countries", locationService.getAllCountries());
            return "user-register";
        }

        EmployerDtoIn employerDtoIn = new EmployerDtoIn();
        employerDtoIn.setUsername(userDtoIn.getUsername());
        employerDtoIn.setPassword(userDtoIn.getPassword());
        employerDtoIn.setConfirmPassword(userDtoIn.getConfirmPassword());
        employerDtoIn.setEmail(userDtoIn.getEmail());
        employerDtoIn.setLocCountryIsoCode(userDtoIn.getLocCountryIsoCode());
        employerDtoIn.setLocCityId(userDtoIn.getLocCityId());

        session.setAttribute("employerFromSession", employerDtoIn);
        model.addAttribute("employer", employerDtoIn);
        session.setAttribute("userDtoIn", userDtoIn);
        return "employer-register";
    }

    @PostMapping("/employer/register/submit")
    public String handleEmployerRegister(@ModelAttribute("employer") EmployerDtoIn employerDtoIn,
                                       BindingResult bindingResult,
                                       HttpSession session) {

        EmployerDtoIn employerFromSession = (EmployerDtoIn) session.getAttribute("employerFromSession");

        if (employerFromSession == null) {
            return "error-view";
        } else {
            employerDtoIn.setUsername(employerFromSession.getUsername());
            employerDtoIn.setPassword(employerFromSession.getPassword());
            employerDtoIn.setConfirmPassword(employerFromSession.getConfirmPassword());
            employerDtoIn.setEmail(employerFromSession.getEmail());
            employerDtoIn.setLocCountryIsoCode(employerFromSession.getLocCountryIsoCode());
            employerDtoIn.setLocCityId(employerFromSession.getLocCityId());
        }

        try {
            employersService.findByCompanyName(employerDtoIn.getCompanyName());
            bindingResult.rejectValue("companyName", "companyName.exists", COMPANY_NAME_IS_ALREADY_TAKEN);
        } catch (EntityNotFoundException ignored) {
        }

        if (bindingResult.hasErrors()) {
            return "employer-register";
        }

        employersService.createEmployer(employerDtoIn);
        Employer employer = employersService.getEmployer(employerDtoIn.getUsername());
        session.setAttribute("currentUser", employer.getUsername());
        session.setAttribute("userRole", employer.getRoles());
        session.removeAttribute("employerFromSession");
        session.removeAttribute("userDtoIn");

        return "redirect:/employers/job-applications";
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                              BindingResult bindingResult,
                              HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            UserPrincipal userPrincipal = authenticationService.login(loginDto.getUsername(), loginDto.getPassword());

            if (userPrincipal.getRoles().equals("EMPLOYER")) {
                Employer employer = employersService.getEmployer(userPrincipal.getId());
                session.setAttribute("currentUser", employer.getUsername());
                session.setAttribute("userRole", employer.getRoles());
            } else if (userPrincipal.getRoles().equals("PROFESSIONAL")) {
                Professional professional = professionalService.getProfessional(userPrincipal.getId());
                session.setAttribute("currentUser", professional.getUsername());
                session.setAttribute("userRole", professional.getRoles());
            } else {
                session.setAttribute("currentUser", userPrincipal.getUsername());
                session.setAttribute("userRole", userPrincipal.getRoles());
            }

            return "redirect:/";

        } catch (EntityNotFoundException e) {
            return "error-view";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.removeAttribute("userRole");
        return "redirect:/";
    }
}

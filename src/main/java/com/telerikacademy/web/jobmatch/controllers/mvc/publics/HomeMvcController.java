package com.telerikacademy.web.jobmatch.controllers.mvc.publics;

import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.services.contracts.JobAdService;
import com.telerikacademy.web.jobmatch.services.contracts.JobApplicationService;
import com.telerikacademy.web.jobmatch.services.contracts.ProfessionalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeMvcController {

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private JobAdService jobAdService;

    @Autowired
    private JobApplicationService jobApplicationService;

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(HttpSession session) { return session.getAttribute("currentUser") != null; }

    @GetMapping
    public String homeView(Model model,
                           HttpSession session) {


        if (isAuthenticated(session)) {
            if (session.getAttribute("userRole").equals("PROFESSIONAL")) {

                Set<Skill> uniqueSkills = jobAdService.getAllUniqueSkillsUsedInJobAds();
                List<Skill> limitedSkills = new ArrayList<>(uniqueSkills).subList(0, 8);
                model.addAttribute("skills", limitedSkills);
            } else if (session.getAttribute("userRole").equals("EMPLOYER")) {

                Set<Skill> uniqueSkills = jobApplicationService.getAllUniqueSkillsUsedInJobApplications();
                List<Skill> limitedSkills = new ArrayList<>(uniqueSkills).subList(0, 8);
                model.addAttribute("skills", limitedSkills);
            }
        } else {

            Set<Skill> uniqueSkills = jobAdService.getAllUniqueSkillsUsedInJobAds();
            List<Skill> limitedSkills = new ArrayList<>(uniqueSkills).subList(0, 8);
            model.addAttribute("skills", limitedSkills);
        }

        model.addAttribute("numberOfApplicants", professionalService.getProfessionals().size());
        model.addAttribute("featuredJobs", jobAdService.getFeaturedAds());
        return "index";
    }
}

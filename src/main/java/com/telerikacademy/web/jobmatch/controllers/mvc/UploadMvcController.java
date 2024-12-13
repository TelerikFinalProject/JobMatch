package com.telerikacademy.web.jobmatch.controllers.mvc;

import com.cloudinary.Cloudinary;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.services.contracts.UploadService;
import com.telerikacademy.web.jobmatch.services.contracts.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadMvcController {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private UserService userService;

    @PostMapping("/profile-picture/{id}")
    public String handleProfilePictureUpload(@PathVariable int id,
                                             HttpSession session,
                                             MultipartFile profilePicture,
                                             HttpServletRequest request) {

        if (session.getAttribute("currentUser") == null) {
            return "error-view";
        }

        if (profilePicture.isEmpty()) {
            return "error-view";
        }

        UserPrincipal user = userService.findByUsername(session.getAttribute("currentUser").toString());

        if (user.getId() != id) {
            return "error-view";
        }

        try {
            uploadService.uploadImage(user.getId(), profilePicture);

        } catch (Exception e) {
            return "error-view";
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }
}

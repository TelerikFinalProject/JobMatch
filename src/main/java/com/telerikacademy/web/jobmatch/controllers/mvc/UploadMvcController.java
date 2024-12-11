package com.telerikacademy.web.jobmatch.controllers.mvc;

import com.cloudinary.Cloudinary;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.services.contracts.UploadService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadMvcController {

    @Autowired
    private UploadService uploadService;

    @GetMapping("/profile-picture")
    public String getProfilePictureTestUpload() {
        return "upload-picture-test";
    }

    @PostMapping("/profile-picture")
    public String handleProfilePictureUpload(HttpSession session,
                                             MultipartFile profilePicture,
                                             Model model) {

        if (session.getAttribute("currentUser") == null) {
            return "error-view";
        }
        if (profilePicture.isEmpty()) {
            return "error-view";
        }

        UserPrincipal user = (UserPrincipal) session.getAttribute("currentUser");
        try {
            uploadService.uploadImage(user.getId(), profilePicture);

        } catch (Exception e) {
            return "error-view";
        }

        return "redirect:/";
    }
}

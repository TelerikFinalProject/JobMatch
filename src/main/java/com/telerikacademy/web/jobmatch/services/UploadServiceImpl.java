package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.services.contracts.CloudinaryService;
import com.telerikacademy.web.jobmatch.services.contracts.UploadService;
import com.telerikacademy.web.jobmatch.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    @Override
    public void uploadImage(int id, MultipartFile file) {
        try {
            userService.uploadImageUrl(id, cloudinaryService.uploadFile(file, "profile_pictures"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

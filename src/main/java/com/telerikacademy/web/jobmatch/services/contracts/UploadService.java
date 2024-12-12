package com.telerikacademy.web.jobmatch.services.contracts;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    void uploadImage(int id, MultipartFile file);
}

package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Status;

public interface StatusService {
    Status getStatus(int id);

    Status getStatus(String status);
}

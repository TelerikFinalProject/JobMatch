package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Status;

public interface StatusRepository {
    Status getStatus(int id);

    Status getStatus(String status);
}

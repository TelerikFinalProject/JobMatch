package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.repositories.contracts.StatusRepository;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public Status getStatus(int id) {
        return statusRepository.getStatus(id);
    }

    @Override
    public Status getStatus(String status) {
        return statusRepository.getStatus(status);
    }
}

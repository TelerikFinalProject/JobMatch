package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.repositories.contracts.EmployerRepository;
import com.telerikacademy.web.jobmatch.services.contracts.EmployersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerServiceImpl implements EmployersService {

    private final EmployerRepository employerRepository;

    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Override
    public List<Employer> getEmployers() {
        return employerRepository.getEmployers();
    }

    @Override
    public Employer getEmployerById(int id) {
        return employerRepository.getEmployerById(id);
    }

    @Override
    public void createEmployer(Employer employer) {
        employerRepository.createEmployer(employer);
    }

    @Override
    public void updateEmployer(Employer updatedUser, Employer user) {

    }

    @Override
    public void deleteEmployer(int id) {

    }

    @Override
    public Employer getUserByUsername(String username) {
        return null;
    }
}

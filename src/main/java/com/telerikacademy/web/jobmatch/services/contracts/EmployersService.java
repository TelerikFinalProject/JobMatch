package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Employer;

import java.util.List;

public interface EmployersService {
    List<Employer> getEmployers();

    Employer getEmployerById(int id);

    void createEmployer(Employer user);

    void updateEmployer(Employer updatedUser, Employer user);

    void deleteEmployer(int id);

    Employer getUserByUsername(String username);
}

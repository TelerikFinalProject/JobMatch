package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Employer;

import java.util.List;

public interface EmployerRepository {
    List<Employer> getEmployers();

    Employer getEmployerById(int id);

    void createEmployer(Employer user);

    void updateEmployer(Employer updatedUser);

    void deleteUser(int id);

    Employer getUserByUsername(String username);
}

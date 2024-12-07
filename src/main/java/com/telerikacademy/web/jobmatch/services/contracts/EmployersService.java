package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerDtoIn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface EmployersService {
    List<Employer> getEmployers();

    Employer getEmployer(int id);

    Employer getEmployer(String username);

    void createEmployer(EmployerDtoIn employerDtoIn);

    void updateEmployer(Employer updatedUser);

    void deleteEmployer(int id);

    Employer findByCompanyName(String companyName);
}

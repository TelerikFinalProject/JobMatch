package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployerRepositoryTest extends JpaRepository<Employer, Integer> {
    Optional<Employer> findByCompanyName(String companyName);
}

package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {

}

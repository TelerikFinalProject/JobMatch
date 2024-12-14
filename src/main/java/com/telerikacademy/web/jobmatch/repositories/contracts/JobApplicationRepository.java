package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.JobApplication;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer>, JpaSpecificationExecutor<JobApplication> {

    @Query(value = "SELECT j.* FROM job_applications j " +
            "JOIN qualifications s ON j.id = s.job_application_id " +
            "WHERE s.skill_id = :skill_id", nativeQuery = true)
    List<JobApplication> findAllBySkill(@Param("skill_id") int skillId);
}

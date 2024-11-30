package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAdRepository extends JpaRepository<JobAd, Integer>, JpaSpecificationExecutor<JobAd> {
}

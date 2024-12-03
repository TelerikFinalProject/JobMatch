package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobMatchRepository extends JpaRepository<JobAd, Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete from job_ads_match_requests where job_ad_id = :id", nativeQuery = true)
    void deleteJobAdInJobAdReq(int id);

    @Modifying
    @Transactional
    @Query(value = "delete from job_ads_match_requests where job_application_id = :id", nativeQuery = true)
    void deleteJobAppInJobAdReq(int id);

    @Modifying
    @Transactional
    @Query(value = "delete from job_applications_match_requests where job_ad_id = :id", nativeQuery = true)
    void deleteJobAdInJobAppReq(int id);

    @Modifying
    @Transactional
    @Query(value = "delete from job_applications_match_requests where job_application_id = :id", nativeQuery = true)
    void deleteJobAppInJobAppReq(int id);
}

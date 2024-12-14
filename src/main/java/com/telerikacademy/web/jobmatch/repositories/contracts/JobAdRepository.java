package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.JobAd;
import com.telerikacademy.web.jobmatch.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAdRepository extends JpaRepository<JobAd, Integer>, JpaSpecificationExecutor<JobAd> {

    @Query(value = "SELECT j.* FROM job_ads j " +
            "JOIN skills_required s ON j.id = s.job_ad_id " +
            "WHERE s.skill_id = :skill_id", nativeQuery = true)
    List<JobAd> findAllBySkill(@Param("skill_id") int skillId);

    @Query(value = "SELECT * FROM job_ads ORDER BY id DESC LIMIT 4", nativeQuery = true)
    List<JobAd> findFeaturedJobAds();
}

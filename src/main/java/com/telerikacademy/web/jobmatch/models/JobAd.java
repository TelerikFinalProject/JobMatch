package com.telerikacademy.web.jobmatch.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "job_ads")
@Getter
@Setter
@NoArgsConstructor
public class JobAd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "position_title")
    private String positionTitle;

    @Column(name = "min_salary")
    private BigDecimal minSalary;

    @Column(name = "max_salary")
    private BigDecimal maxSalary;

    @Column(name = "job_description")
    private String jobDescription;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToMany
    @JoinTable(
            name = "match_requests",
            joinColumns = @JoinColumn(name = "job_ad_id"),
            inverseJoinColumns = @JoinColumn(name = "job_application_id")
    )
    private Set<JobApplication> matchedApplications;
}

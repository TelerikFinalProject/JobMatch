package com.telerikacademy.web.jobmatch.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
public class Professional extends UserPrincipal {
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "summary")
    private String summary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToMany
    @JoinTable(
            name = "successful_matches",
            joinColumns = @JoinColumn(name = "professional_id"),
            inverseJoinColumns = @JoinColumn(name = "employer_id")
    )
    private Set<Employer> successfulMatches;
}

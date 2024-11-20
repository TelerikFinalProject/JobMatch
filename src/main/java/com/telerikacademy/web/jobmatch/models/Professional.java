package com.telerikacademy.web.jobmatch.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
public class Professional extends UserPrincipal{
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "summary")
    private String summary;
}

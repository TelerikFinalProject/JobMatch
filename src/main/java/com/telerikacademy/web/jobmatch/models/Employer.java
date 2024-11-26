package com.telerikacademy.web.jobmatch.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employers", uniqueConstraints = @UniqueConstraint(columnNames = "company_name"))
@Getter
@Setter
@NoArgsConstructor
public class Employer extends UserPrincipal{
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "description")
    private String description;
}

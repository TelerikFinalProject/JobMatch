package com.telerikacademy.web.jobmatch.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

package com.telerikacademy.web.jobmatch.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(columnNames = "city_name"))
public class Location {
    @Id
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "city_name")
    private String name;

    @Column(name = "country_iso")
    private String isoCode;
}

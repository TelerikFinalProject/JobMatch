package com.telerikacademy.web.jobmatch.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // Increase the length to a value that can accommodate your actual token lengths
    @Column(name = "refresh_token", nullable = false, length = 10000)
    private String refreshToken;

    @Column(name = "revoked")
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserPrincipal user;

}

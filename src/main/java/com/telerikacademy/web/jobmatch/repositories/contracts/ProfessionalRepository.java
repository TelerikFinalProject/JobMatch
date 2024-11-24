package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Professional;

import java.util.List;

public interface ProfessionalRepository {
    List<Professional> getProfessionals();

    Professional getProfessional(int id);

    Professional getProfessional(String username);

    void registerProfessional(Professional professional);

    void updateProfessional(Professional professional);

    void deleteProfessional(int id);
}

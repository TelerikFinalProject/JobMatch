package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalDtoIn;

import java.util.List;

public interface ProfessionalService {
    List<Professional> getProfessionals();

    Professional getProfessional(int id);

    Professional getProfessionalByUsername(String username);

    Professional getProfessionalByEmail(String email);

    void registerProfessional(ProfessionalDtoIn professionalDtoIn);

    void updateProfessional(Professional professional);

    void deleteProfessional(int id);
}

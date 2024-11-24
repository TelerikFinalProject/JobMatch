package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;

import java.util.List;

public interface ProfessionalService {
    List<Professional> getProfessionals(UserPrincipal user);

    Professional getProfessional(UserPrincipal user, int id);

    Professional getProfessional(UserPrincipal user, String username);

    void registerProfessional(Professional professional);

    void updateProfessional(UserPrincipal user, Professional professional);

    void deleteProfessional(UserPrincipal user, int id);
}

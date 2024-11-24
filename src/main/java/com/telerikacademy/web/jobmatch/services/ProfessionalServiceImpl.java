package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.AuthorizationException;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.repositories.contracts.ProfessionalRepository;
import com.telerikacademy.web.jobmatch.services.contracts.ProfessionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    @Autowired
    public ProfessionalServiceImpl(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @Override
    public List<Professional> getProfessionals(UserPrincipal user) {
        authorizationCheck(user,"access");

        return professionalRepository.getProfessionals();
    }

    @Override
    public Professional getProfessional(UserPrincipal user, int id) {
        authorizationCheck(user, id, "access");

        return professionalRepository.getProfessional(id);
    }

    @Override
    public Professional getProfessional(UserPrincipal user, String username) {
        if (!user.getRole().getRole().equals("ROLE_ADMIN") || user.getUsername().equals(username)) {
            throw new AuthorizationException("access", "recourse");
        }

        return professionalRepository.getProfessional(username);
    }

    @Override
    public void registerProfessional(Professional professional) {
        professionalRepository.registerProfessional(professional);
    }

    @Override
    public void updateProfessional(UserPrincipal user, Professional professional) {
        authorizationCheck(user, professional.getId(), "update");
        
        professionalRepository.updateProfessional(professional);
    }

    @Override
    public void deleteProfessional(UserPrincipal user, int id) {
        authorizationCheck(user, id, "delete");

        professionalRepository.deleteProfessional(id);
    }

    private static void authorizationCheck(UserPrincipal user, int id, String action) {
        if (!user.getRole().getRole().equals("ROLE_ADMIN") || user.getId() != id) {
            throw new AuthorizationException(action, "recourse");
        }
    }

    private static void authorizationCheck(UserPrincipal user, String action) {
        if (!user.getRole().getRole().equals("ROLE_ADMIN")) {
            throw new AuthorizationException(action, "recourse");
        }
    }
}

package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.ProfessionalMappers;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.repositories.contracts.ProfessionalRepository;
import com.telerikacademy.web.jobmatch.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final StatusService statusService;

    @Autowired
    public ProfessionalServiceImpl(ProfessionalRepository professionalRepository,
                                   UserService userService,
                                   LocationService locationService,
                                   StatusService statusService) {
        this.professionalRepository = professionalRepository;
        this.userService = userService;
        this.locationService = locationService;
        this.statusService = statusService;
    }

    @Override
    public List<Professional> getProfessionals() {
        return professionalRepository.findAll();
    }

    @Override
    public Professional getProfessional(int id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professional", id));
    }

    @Override
    public Professional getProfessionalByUsername(String username) {
        UserPrincipal userFound = userService.findByUsername(username);
        return getProfessional(userFound.getId());
    }

    @Override
    public Professional getProfessionalByEmail(String email) {
        UserPrincipal userFound = userService.findByEmail(email);
        return getProfessional(userFound.getId());
    }

    @Override
    public void registerProfessional(ProfessionalDtoIn professionalDtoIn) {
        Professional professionalToCreate = ProfessionalMappers.INSTANCE
                .fromDtoIn(professionalDtoIn, locationService, statusService);
        checkForDuplicateEmail(professionalToCreate);
        checkForDuplicateUsername(professionalToCreate);

        professionalRepository.save(professionalToCreate);
    }

    @Override
    public void updateProfessional(Professional professional) {
        checkForDuplicateEmail(professional.getId(), professional);
        checkForDuplicateUsername(professional.getId(), professional);

        professionalRepository.save(professional);
    }

    @Override
    public void deleteProfessional(int id) {
        Professional professional = getProfessional(id);
        professionalRepository.delete(professional);
    }

    private void checkForDuplicateEmail(Professional professional) {
        boolean duplicateExists = true;
        try {
            getProfessionalByEmail(professional.getEmail());

        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("Employer", "email", professional.getEmail());
        }
    }

    private void checkForDuplicateUsername(Professional professional) {
        boolean duplicateExists = true;
        try {
            getProfessionalByUsername(professional.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("Employer", "username", professional.getUsername());
        }
    }

    private void checkForDuplicateEmail(int id, Professional professional) {
        try {
            checkForDuplicateEmail(professional);
        } catch (EntityDuplicateException e) {
            if (professional.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }

    private void checkForDuplicateUsername(int id, Professional professional) {
        try {
            checkForDuplicateUsername(professional);
        } catch (EntityDuplicateException e) {
            if (professional.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }
}

package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
import com.telerikacademy.web.jobmatch.helpers.ProfessionalMappers;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.ProfessionalUpdateDto;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.ProfessionalService;
import com.telerikacademy.web.jobmatch.services.contracts.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalRestController {
    private final ProfessionalService professionalService;
    private final LocationService locationService;
    private final StatusService statusService;

    @Autowired
    public ProfessionalRestController(ProfessionalService professionalService,
                                      LocationService locationService,
                                      StatusService statusService) {
        this.professionalService = professionalService;
        this.locationService = locationService;
        this.statusService = statusService;
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalOutDto>> getAllProfessionals(Authentication authentication) {
        authentication.getAuthorities();
        List<ProfessionalOutDto> professionalOutDtoList = ProfessionalMappers.INSTANCE
                .toDtoOutList(professionalService.getProfessionals());
        return ResponseEntity.ok(professionalOutDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalOutDto> getProfessionalById(@PathVariable int id) {
        try {
            ProfessionalOutDto professional = ProfessionalMappers.INSTANCE
                    .toDtoOut(professionalService.getProfessional(id));
            return ResponseEntity.ok(professional);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProfessionalOutDto> updateProfessional(@PathVariable int id,
                                                                 @RequestBody ProfessionalUpdateDto professionalDto) {
        Professional professionalToUpdate;
        try {
            professionalToUpdate = professionalService.getProfessional(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        if (!professionalDto.getCurrentPassword().equals(professionalToUpdate.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password do not match");
        }
        if (!professionalDto.getPassword().equals(professionalDto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        try {
            Professional updatedProfessional = ProfessionalMappers.INSTANCE
                    .fromDtoIn(professionalToUpdate, professionalDto, locationService, statusService);
            professionalService.updateProfessional(updatedProfessional);
            return ResponseEntity.ok(ProfessionalMappers.INSTANCE.toDtoOut(updatedProfessional));
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfessional(@PathVariable int id) {
        try {
            professionalService.deleteProfessional(id);
            return ResponseEntity.ok("Professional deleted");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

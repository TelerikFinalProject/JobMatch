package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
import com.telerikacademy.web.jobmatch.helpers.ProfessionalMappers;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalDtoIn;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.ProfessionalUpdateDto;
import com.telerikacademy.web.jobmatch.services.contracts.ProfessionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalRestController {
    private final ProfessionalService professionalService;

    @Autowired
    public ProfessionalRestController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalOutDto>> getAllProfessionals() {
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

    @PostMapping("/register")
    public ResponseEntity<String> createProfessional(@RequestBody ProfessionalDtoIn professionalDtoIn) {
        if (!professionalDtoIn.getPassword().equals(professionalDtoIn.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        try {
            professionalService.registerProfessional(professionalDtoIn);
            return ResponseEntity.ok("Employer created");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
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
                    .fromDtoIn(professionalToUpdate, professionalDto);
            professionalService.updateProfessional(updatedProfessional);
            return ResponseEntity.ok(ProfessionalMappers.INSTANCE.toDtoOut(updatedProfessional));
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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

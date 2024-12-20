package com.telerikacademy.web.jobmatch.controllers.rest;

import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.exceptions.ExternalResourceException;
import com.telerikacademy.web.jobmatch.helpers.EmployerMappers;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerOutDto;
import com.telerikacademy.web.jobmatch.models.dtos.users.EmployerUpdateDto;
import com.telerikacademy.web.jobmatch.services.contracts.EmployersService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerRestController {

    private final EmployersService employersService;
    private final LocationService locationService;

    @Autowired
    public EmployerRestController(EmployersService employersService,
                                  LocationService locationService) {
        this.employersService = employersService;
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<EmployerOutDto>> getAllEmployers() {
        List<EmployerOutDto> employerToCreate = EmployerMappers.INSTANCE.toDtoOutList(employersService.getEmployers());
        return ResponseEntity.ok(employerToCreate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployerOutDto> getEmployerById(@PathVariable int id) {
        try {
            EmployerOutDto employerOutDto = EmployerMappers.INSTANCE.toDtoOut(employersService.getEmployer(id));
            return ResponseEntity.ok(employerOutDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployerOutDto> updateEmployer(@PathVariable int id,
                                                         @RequestBody EmployerUpdateDto employerDto) {
        Employer employerToUpdate;
        try {
            employerToUpdate = employersService.getEmployer(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        if (!employerDto.getCurrentPassword().equals(employerToUpdate.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password do not match");
        }
        if (!employerDto.getPassword().equals(employerDto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        try {
            Employer updatedEmployer = EmployerMappers.INSTANCE.fromDtoIn(employerToUpdate, employerDto, locationService);
            employersService.updateEmployer(updatedEmployer);
            return ResponseEntity.ok(EmployerMappers.INSTANCE.toDtoOut(updatedEmployer));
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ExternalResourceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployer(@PathVariable int id) {
        try {
            employersService.deleteEmployer(id);
            return ResponseEntity.ok("Employer deleted");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityDuplicateException;
import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.EmployerMappers;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.EmployerDtoIn;
import com.telerikacademy.web.jobmatch.repositories.contracts.EmployerRepository;
import com.telerikacademy.web.jobmatch.services.contracts.EmployersService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.RoleService;
import com.telerikacademy.web.jobmatch.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerServiceImpl implements EmployersService {

    private final EmployerRepository employerRepository;
    private final RoleService roleService;
    private final LocationService locationService;
    private final UserService userService;

    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository,
                               RoleService roleService,
                               LocationService locationService,
                               UserService userService) {
        this.employerRepository = employerRepository;
        this.roleService = roleService;
        this.locationService = locationService;
        this.userService = userService;
    }

    @Override
    public List<Employer> getEmployers() {
        return employerRepository.getEmployers();
    }

    @Override
    public Employer getEmployer(int id) {
        return employerRepository.getEmployer(id);
    }

    @Override
    public Employer getEmployer(String username) {
        UserPrincipal user = userService.findByUsername(username);
        return employerRepository.getEmployer(user.getId());
    }


    @Override
    public void createEmployer(EmployerDtoIn employerDtoIn) {
        Employer employerToCreate = EmployerMappers.INSTANCE.fromDtoIn(employerDtoIn);
        checkForDuplicateEmail(employerToCreate);
        checkForDuplicateUsername(employerToCreate);
        checkForDuplicateCompanyName(employerToCreate);
        employerToCreate.setRole(roleService.getRole("ROLE_EMPLOYER"));

        employerToCreate.setLocation(locationService.returnIfExistOrCreate(employerDtoIn.getLocCountryIsoCode(),
                employerDtoIn.getLocCityId()));

        employerRepository.createEmployer(employerToCreate);
    }

    @Override
    public void updateEmployer(Employer updatedUser) {
        checkForDuplicateUsername(updatedUser.getId(), updatedUser);
        checkForDuplicateCompanyName(updatedUser.getId(), updatedUser);
        checkForDuplicateEmail(updatedUser.getId(), updatedUser);

        employerRepository.updateEmployer(updatedUser);
    }

    @Override
    public void deleteEmployer(int id) {
        employerRepository.deleteUser(id);
    }

    private void checkForDuplicateEmail(Employer employer) {
        boolean duplicateExists = true;
        try {
            userService.findByEmail(employer.getEmail());

        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("Employer", "email", employer.getEmail());
        }
    }

    private void checkForDuplicateUsername(Employer employer) {
        boolean duplicateExists = true;
        try {
            getEmployer(employer.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("Employer", "username", employer.getUsername());
        }
    }

    private void checkForDuplicateCompanyName(Employer employer) {
        boolean duplicateExists = true;
        try {
            employerRepository.getEmployerByCompanyName(employer.getCompanyName());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("Employer", "company name", employer.getCompanyName());
        }
    }

    private void checkForDuplicateEmail(int id, Employer employer) {
        try{
            checkForDuplicateEmail(employer);
        } catch (EntityDuplicateException e){
            if (employer.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }

    private void checkForDuplicateUsername(int id, Employer employer) {
        try {
            checkForDuplicateUsername(employer);
        } catch (EntityDuplicateException e){
            if (employer.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }

    private void checkForDuplicateCompanyName(int id, Employer employer) {
        try {
            checkForDuplicateCompanyName(employer);
        } catch (EntityDuplicateException e){
            if (employer.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }
}

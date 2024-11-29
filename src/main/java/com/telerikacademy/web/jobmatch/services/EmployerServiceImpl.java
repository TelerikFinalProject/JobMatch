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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployerServiceImpl implements EmployersService {

    private final EmployerRepository employerRepository;
    private final LocationService locationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository,
                               LocationService locationService,
                               UserService userService,
                               PasswordEncoder passwordEncoder,
                               RoleService roleService) {
        this.employerRepository = employerRepository;
        this.locationService = locationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public List<Employer> getEmployers() {
        return employerRepository.findAll();
    }

    @Override
    public Employer getEmployer(int id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employer", id));
    }

    @Override
    public Employer getEmployer(String username) {
        UserPrincipal user = userService.findByUsername(username);
        return employerRepository.getReferenceById(user.getId());
    }


    @Override
    public void createEmployer(EmployerDtoIn employerDtoIn) {
        Employer employerToCreate = EmployerMappers.INSTANCE.fromDtoIn(employerDtoIn, locationService, roleService);
        employerToCreate.setPassword(passwordEncoder.encode(employerToCreate.getPassword()));

        checkForDuplicateEmail(employerToCreate);
        checkForDuplicateUsername(employerToCreate);
        checkForDuplicateCompanyName(employerToCreate);

        employerRepository.save(employerToCreate);
    }

    @Override
    public void updateEmployer(Employer updatedUser) {
        checkForDuplicateUsername(updatedUser.getId(), updatedUser);
        checkForDuplicateCompanyName(updatedUser.getId(), updatedUser);
        checkForDuplicateEmail(updatedUser.getId(), updatedUser);


        employerRepository.save(updatedUser);
    }

    @Override
    public void deleteEmployer(int id) {
        Employer employer = getEmployer(id);
        employerRepository.delete(employer);
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
            employerRepository.findByCompanyName(employer.getCompanyName())
                    .orElseThrow(() -> new EntityNotFoundException("Employer", "company name", employer.getCompanyName()));
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("Employer", "company name", employer.getCompanyName());
        }
    }

    private void checkForDuplicateEmail(int id, Employer employer) {
        try {
            checkForDuplicateEmail(employer);
        } catch (EntityDuplicateException e) {
            if (employer.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }

    private void checkForDuplicateUsername(int id, Employer employer) {
        try {
            checkForDuplicateUsername(employer);
        } catch (EntityDuplicateException e) {
            if (employer.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }

    private void checkForDuplicateCompanyName(int id, Employer employer) {
        try {
            checkForDuplicateCompanyName(employer);
        } catch (EntityDuplicateException e) {
            if (employer.getId() != id) {
                throw new EntityDuplicateException(e.getMessage());
            }
        }
    }
}

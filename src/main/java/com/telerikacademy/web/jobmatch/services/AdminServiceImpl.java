package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.helpers.AdminMappers;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.users.UserDtoIn;
import com.telerikacademy.web.jobmatch.repositories.contracts.UserRepository;
import com.telerikacademy.web.jobmatch.services.contracts.AdminService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final LocationService locationService;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            LocationService locationService) {
        this.userRepository = userRepository;
        this.locationService = locationService;
    }

    @Override
    public List<UserPrincipal> getAdmins() {
        //TODO only admins can access this resource
        return userRepository.findAllByRole("ADMIN");
    }

    @Override
    public void createAdmin(UserDtoIn userDtoIn) {
        //TODO only admins can access this resource
        UserPrincipal admin = AdminMappers.INSTANCE.fromDtoIn(userDtoIn, locationService);
        userRepository.save(admin);
    }

    @Override
    public void updateAdmin(UserPrincipal updatedUser) {
        userRepository.save(updatedUser);
    }

    @Override
    public void deleteAdmin(int id) {
        UserPrincipal user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Admin", id));
        if (!user.getRoles().equals("ADMIN")) {
            throw new EntityNotFoundException("Admin", id);
        }
        userRepository.delete(user);
    }
}

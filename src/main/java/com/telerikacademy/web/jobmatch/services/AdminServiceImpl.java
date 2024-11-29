package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.helpers.AdminMappers;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.models.dtos.UserDtoIn;
import com.telerikacademy.web.jobmatch.repositories.contracts.AdminRepository;
import com.telerikacademy.web.jobmatch.services.contracts.AdminService;
import com.telerikacademy.web.jobmatch.services.contracts.LocationService;
import com.telerikacademy.web.jobmatch.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final LocationService locationService;
    private final RoleService roleService;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository,
                            LocationService locationService,
                            RoleService roleService) {
        this.adminRepository = adminRepository;
        this.locationService = locationService;
        this.roleService = roleService;
    }

    @Override
    public List<UserPrincipal> getAdmins() {
        //TODO only admins can access this resource
        return adminRepository.getAdmins();
    }

    @Override
    public UserPrincipal getAdmin(int id) {
        //TODO only admins can access this resource
        return adminRepository.getAdmin(id);
    }

    @Override
    public UserPrincipal getAdmin(String username) {
        //TODO only admins can access this resource
        return adminRepository.getAdmin(username);
    }

    @Override
    public void createAdmin(UserDtoIn userDtoIn) {
        //TODO only admins can access this resource
        UserPrincipal admin = AdminMappers.INSTANCE.fromDtoIn(userDtoIn, locationService, roleService);
        adminRepository.createAdmin(admin);
    }

    @Override
    public void updateAdmin(UserPrincipal updatedUser) {
        adminRepository.updateAdmin(updatedUser);
    }

    @Override
    public void deleteAdmin(int id) {
        UserPrincipal user = adminRepository.getAdmin(id);
        adminRepository.deleteAdmin(user);
    }
}

package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Role;
import com.telerikacademy.web.jobmatch.repositories.contracts.RoleRepository;
import com.telerikacademy.web.jobmatch.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRole(String name) {
        return roleRepository.findByRole(name)
                .orElseThrow(() -> new EntityNotFoundException("Role with name " + name + " not found"));
    }
}
package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.repositories.contracts.ProfessionalRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfessionalRepositoryImpl implements ProfessionalRepository {
    @Override
    public List<Professional> getProfessionals() {
        return List.of();
    }

    @Override
    public Professional getProfessional(int id) {
        return null;
    }

    @Override
    public Professional getProfessional(String username) {
        return null;
    }

    @Override
    public void registerProfessional(Professional professional) {

    }

    @Override
    public void updateProfessional(Professional professional) {

    }

    @Override
    public void deleteProfessional(int id) {

    }
}

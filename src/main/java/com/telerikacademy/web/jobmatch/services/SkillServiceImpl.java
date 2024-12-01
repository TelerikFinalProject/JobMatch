package com.telerikacademy.web.jobmatch.services;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.repositories.contracts.SkillRepository;
import com.telerikacademy.web.jobmatch.services.contracts.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Autowired
    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill getSkill(int id) {
        return skillRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Skill", id));
    }

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public Skill getSkillByName(String name) {
        return skillRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Skill", "name", name));
    }

    @Override
    public Skill addSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public void updateSkill(Skill skill) {
        skillRepository.save(skill);
    }


    @Override
    public void deleteSkill(int id) {
        Skill skillToDelete = skillRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Skill", id));
        skillRepository.delete(skillToDelete);
    }
}

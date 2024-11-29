package com.telerikacademy.web.jobmatch.repositories.contracts;

import com.telerikacademy.web.jobmatch.models.Skill;

import java.util.List;

public interface SkillRepository {
    List<Skill> getAllSkills();

    Skill getSkillById(int id);

    Skill getSkillByName(String name);

    void saveSkill(Skill skill);

    void deleteSkill(Skill skill);
}

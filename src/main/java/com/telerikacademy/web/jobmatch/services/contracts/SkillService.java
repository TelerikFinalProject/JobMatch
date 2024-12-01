package com.telerikacademy.web.jobmatch.services.contracts;

import com.telerikacademy.web.jobmatch.models.Skill;

import java.util.List;

public interface SkillService {
    Skill getSkill(int id);

    List<Skill> getAllSkills();

    Skill getSkillByName(String name);

    Skill addSkill(Skill skill);

    void updateSkill(Skill skill);

    void deleteSkill(int id);
}

package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Skill;
import com.telerikacademy.web.jobmatch.repositories.contracts.SkillRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkillRepositoryImpl implements SkillRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public SkillRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<Skill> getAllSkills() {
        try (Session session = sessionFactory.openSession()) {
            Query<Skill> query = session.createQuery("from Skill", Skill.class);
            return query.list();
        }
    }

    @Override
    public Skill getSkillById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Skill skill = session.get(Skill.class, id);
            if (skill == null) {
                throw new EntityNotFoundException("Skill", id);
            }
            return skill;
        }
    }

    @Override
    public Skill getSkillByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Skill> query = session.createQuery("from Skill where name = :name", Skill.class);
            query.setParameter("name", name);

            List<Skill> skills = query.list();
            if (skills.isEmpty()) {
                throw new EntityNotFoundException("Skill", "name", name);
            }
            return skills.get(0);
        }
    }

    @Override
    public void saveSkill(Skill skill) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(skill);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteSkill(Skill skill) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(skill);
            session.getTransaction().commit();
        }
    }
}

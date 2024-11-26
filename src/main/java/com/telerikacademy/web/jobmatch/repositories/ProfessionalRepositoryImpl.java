package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Professional;
import com.telerikacademy.web.jobmatch.repositories.contracts.ProfessionalRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfessionalRepositoryImpl implements ProfessionalRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ProfessionalRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Professional> getProfessionals() {
        try (Session session = sessionFactory.openSession()) {
            Query<Professional> query = session.createQuery("from Professional", Professional.class);
            return query.list();
        }
    }

    @Override
    public Professional getProfessional(int id) {
        try (Session session = sessionFactory.openSession()) {
            Professional professional = session.get(Professional.class, id);

            if (professional == null) {
                throw new EntityNotFoundException("Professional", id);
            }
            return professional;
        }
    }

    @Override
    public void registerProfessional(Professional professional) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(professional);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateProfessional(Professional professional) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(professional);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteProfessional(int id) {
        try (Session session = sessionFactory.openSession()) {
            Professional professional = getProfessional(id);
            session.beginTransaction();
            session.remove(professional);
            session.getTransaction().commit();
        }
    }
}

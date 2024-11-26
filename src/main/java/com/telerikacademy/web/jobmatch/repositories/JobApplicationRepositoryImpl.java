package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.models.JobApplication;
import com.telerikacademy.web.jobmatch.repositories.contracts.JobApplicationRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobApplicationRepositoryImpl implements JobApplicationRepository {

    private final SessionFactory sessionFactory;

    public JobApplicationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<JobApplication> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from JobApplication", JobApplication.class).list();
        }
    }

    @Override
    public JobApplication findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(JobApplication.class, id);
        }
    }

    @Override
    public void save(JobApplication jobApplication) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(jobApplication);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(JobApplication jobApplication) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(jobApplication);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(JobApplication jobApplication) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(jobApplication);
            session.getTransaction().commit();
        }
    }
}

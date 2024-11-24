package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.models.JobAd;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JobAdRepositoryImpl implements JobAdRepository {

    private final SessionFactory sessionFactory;

    public JobAdRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<JobAd> findAll() {
        List<JobAd> jobAds = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from JobAd", JobAd.class).list();
        }
    }

    public JobAd findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(JobAd.class, id);
        }
    }

    @Override
    public void save(JobAd jobAd) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(jobAd);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(JobAd jobAd) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(jobAd);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(JobAd jobAd) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(jobAd);
            session.getTransaction().commit();
        }
    }
}

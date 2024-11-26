package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Employer;
import com.telerikacademy.web.jobmatch.repositories.contracts.EmployerRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployerRepositoryImpl implements EmployerRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public EmployerRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Employer> getEmployers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Employer> query = session.createQuery("from Employer", Employer.class);
            return query.list();
        }
    }

    @Override
    public Employer getEmployer(int id) {
        try (Session session = sessionFactory.openSession()) {
            Employer employer = session.get(Employer.class, id);
            if (employer == null) {
                throw new EntityNotFoundException("Employer", id);
            }
            return employer;
        }
    }

    @Override
    public Employer getEmployerByCompanyName(String companyName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Employer> employerQuery = session.createQuery("from Employer where companyName = :companyName", Employer.class);
            employerQuery.setParameter("companyName", companyName);
            List<Employer> employer = employerQuery.list();

            if (employer.isEmpty()) {
                throw new EntityNotFoundException("Employer", "company name", companyName);
            }
            return employer.get(0);
        }
    }

    @Override
    public void createEmployer(Employer employer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(employer);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateEmployer(Employer updatedEmployer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(updatedEmployer);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteUser(int id) {
        try (Session session = sessionFactory.openSession()) {
            Employer employer = getEmployer(id);
            session.beginTransaction();
            session.remove(employer);
            session.getTransaction().commit();
        }
    }

    @Override
    public Employer getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<Employer> query = session.createQuery("from Employer where username = :username", Employer.class);
            query.setParameter("username", username);

            List<Employer> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("Employer", "username", username);
            }

            return users.get(0);
        }
    }
}

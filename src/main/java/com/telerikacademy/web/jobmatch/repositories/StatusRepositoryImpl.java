package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Status;
import com.telerikacademy.web.jobmatch.repositories.contracts.StatusRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatusRepositoryImpl implements StatusRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public StatusRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Status getStatus(int id) {
        try (Session session = sessionFactory.openSession()) {
            Status status = session.get(Status.class, id);
            if (status == null) {
                throw new EntityNotFoundException("Status", id);
            }
            return status;
        }
    }

    @Override
    public Status getStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Status> query = session.createQuery("from Status where status = :status", Status.class);
            query.setParameter("status", status);

            List<Status> statuses = query.list();
            if (statuses.isEmpty()) {
                throw new EntityNotFoundException("Status", "name", status);
            }
            return statuses.get(0);
        }
    }
}

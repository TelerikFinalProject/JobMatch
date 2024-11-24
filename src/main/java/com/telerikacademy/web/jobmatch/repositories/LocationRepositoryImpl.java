package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Location;
import com.telerikacademy.web.jobmatch.repositories.contracts.LocationRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public LocationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Location getLocation(int id) {
        try (Session session = sessionFactory.openSession()) {
            Location location = session.get(Location.class, id);

            if (location == null) {
                throw new EntityNotFoundException("Location", id);
            }

            return location;
        }
    }

    @Override
    public void addLocation(Location location) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(location);
            session.getTransaction().commit();
        }
    }
}

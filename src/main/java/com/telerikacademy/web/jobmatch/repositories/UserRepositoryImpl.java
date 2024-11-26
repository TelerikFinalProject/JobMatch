package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserPrincipal findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserPrincipal> userQuery = session.createQuery("from UserPrincipal where username = :username",
                    UserPrincipal.class);
            userQuery.setParameter("username", username);

            List<UserPrincipal> users = userQuery.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return users.get(0);
        }
    }

    @Override
    public UserPrincipal findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserPrincipal> employerQuery = session.createQuery("from UserPrincipal where email = :email",
                    UserPrincipal.class);
            employerQuery.setParameter("email", email);

            List<UserPrincipal> employer = employerQuery.list();
            if (employer.isEmpty()) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return employer.get(0);
        }
    }

    @Override
    public UserPrincipal findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            UserPrincipal user = session.get(UserPrincipal.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }
}

package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.UserPrincipal;
import com.telerikacademy.web.jobmatch.repositories.contracts.AdminRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public AdminRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<UserPrincipal> getAdmins() {
        try (Session session = sessionFactory.openSession()) {
            Query<UserPrincipal> query = session
                    .createQuery("from UserPrincipal u join u.role r where r.role = :admin", UserPrincipal.class);
            query.setParameter("admin", "ROLE_ADMIN");
            return query.list();
        }
    }

    @Override
    public UserPrincipal getAdmin(int id) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserPrincipal> query = session.createQuery(
                    "from UserPrincipal u join u.role r where r.role = 'ROLE_ADMIN' and u.id = :id", UserPrincipal.class);
            query.setParameter("id", id);

            List<UserPrincipal> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("Admin", id);
            }
            return users.get(0);
        }
    }

    @Override
    public UserPrincipal getAdmin(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserPrincipal> query = session.createQuery(
                    "from UserPrincipal u join u.role r where r.role = 'ROLE_ADMIN' and u.username = :username", UserPrincipal.class);
            query.setParameter("username", username);

            List<UserPrincipal> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("Admin", "username", username);
            }
            return users.get(0);
        }
    }

    @Override
    public void createAdmin(UserPrincipal admin) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(admin);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateAdmin(UserPrincipal updatedUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(updatedUser);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteAdmin(UserPrincipal admin) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(admin);
            session.getTransaction().commit();
        }
    }
}

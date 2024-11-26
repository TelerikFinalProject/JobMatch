package com.telerikacademy.web.jobmatch.repositories;

import com.telerikacademy.web.jobmatch.exceptions.EntityNotFoundException;
import com.telerikacademy.web.jobmatch.models.Role;
import com.telerikacademy.web.jobmatch.repositories.contracts.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role getRole(String roleName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("from Role where role = :name", Role.class);
            query.setParameter("name", roleName);

            List<Role> roles = query.list();
            if (roles.isEmpty()) {
                throw new EntityNotFoundException("Role", "name", roleName);
            }
            return roles.get(0);
        }
    }
}

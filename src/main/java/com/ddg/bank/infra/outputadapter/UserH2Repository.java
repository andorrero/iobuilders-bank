package com.ddg.bank.infra.outputadapter;

import com.ddg.bank.application.exception.DatabaseException;
import com.ddg.bank.application.exception.NonEmptyException;
import com.ddg.bank.domain.entity.User;
import com.ddg.bank.infra.outputport.UserOutputPort;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserH2Repository implements UserOutputPort, PanacheRepositoryBase<User, UUID> {

    @Override
    public User find(final String username, final String password) {
        try {
            return find("username = :username and password = :password",
                    Parameters.with("username", username).and("password", password))
                    .firstResult();
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: User can not be retrieved");
        }
    }

    @Override
    @Transactional
    public User create(final User user) {
        try {
            var userRetrieved = find("username = :username",
                    Parameters.with("username", user.getUsername()))
                    .firstResult();
            if(userRetrieved != null) {
                throw new NonEmptyException("Impossible create user: username already exists");
            }

            persistAndFlush(user);
            return user;
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: User can not be created");
        }
    }

    @Override
    public Optional<User> view(UUID userId) {
        try {
            return findByIdOptional(userId);
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: User can not be retrieved");
        }
    }

}

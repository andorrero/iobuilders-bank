package com.ddg.bank.infra.outputadapter;

import com.ddg.bank.application.exception.DatabaseException;
import com.ddg.bank.application.exception.NonEmptyException;
import com.ddg.bank.domain.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.persistence.PersistenceException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.openMocks;

@QuarkusTest
class UserH2RepositoryTest {

    @InjectMocks
    UserH2Repository userH2Repository;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testFind_givenCorrectUsernameAndPassword_whenExistsUser_thenReturnUser() {
        var expectedUser = new User();
        var userH2RepositorySpy = spy(userH2Repository);

        var panacheQueryMock = mock(PanacheQuery.class);
        doReturn(expectedUser).when(panacheQueryMock).firstResult();
        doReturn(panacheQueryMock).when(userH2RepositorySpy).find(anyString(), any(Parameters.class));

        var responseUser = userH2RepositorySpy.find("user", "pass");
        assertThat(expectedUser, is(responseUser));
    }

    @Test
    void testFind_givenCorrectUsernameAndPassword_whenNotExistsUser_thenReturnNull() {
        var userH2RepositorySpy = spy(userH2Repository);

        var panacheQueryMock = mock(PanacheQuery.class);
        doReturn(null).when(panacheQueryMock).firstResult();
        doReturn(panacheQueryMock).when(userH2RepositorySpy).find(anyString(), any(Parameters.class));

        var responseUser = userH2RepositorySpy.find("user", "pass");
        assertThat(responseUser, nullValue());
    }

    @Test
    void testFind_givenCorrectUsernameAndPassword_whenDatabaseCrash_thenReturnException() {
        var userH2RepositorySpy = spy(userH2Repository);
        doThrow(new PersistenceException("error in find")).when(userH2RepositorySpy).find(anyString(), any(Parameters.class));

        try {
            userH2RepositorySpy.find("user", "pass");
        } catch (DatabaseException databaseException) {
            assertThat(databaseException.getMessage(), is("Error in database: User can not be retrieved"));
        }
    }

    @Test
    void testCreate_givenCorrectUser_whenIsPossibleToCreate_thenReturnUserCreated() {
        var expectedUser = new User();
        var userH2RepositorySpy = spy(userH2Repository);
        doNothing().when(userH2RepositorySpy).persistAndFlush(any(User.class));

        var responseUser = userH2RepositorySpy.create(new User());
        assertThat(expectedUser, is(responseUser));
    }

    @Test
    void testCreate_givenCorrectUser_whenUserExists_thenReturnException() {
        var expectedUser = new User();
        expectedUser.setUsername("same");

        var userH2RepositorySpy = spy(userH2Repository);
        var panacheQueryMock = mock(PanacheQuery.class);
        doReturn(expectedUser).when(panacheQueryMock).firstResult();
        doReturn(panacheQueryMock).when(userH2RepositorySpy).find(anyString(), any(Parameters.class));

        try {
            userH2RepositorySpy.create(expectedUser);
        } catch (NonEmptyException nonEmptyException) {
            assertThat(nonEmptyException.getMessage(), is("Impossible create user: username already exists"));
        }
    }

    @Test
    void testCreate_givenCorrectUser_whenTheDatabaseCrash_thenReturnException() {
        var userH2RepositorySpy = spy(userH2Repository);
        doThrow(new PersistenceException("error in creation")).when(userH2RepositorySpy).persistAndFlush(any(User.class));

        try {
            userH2RepositorySpy.create(new User());
        } catch (DatabaseException databaseException) {
            assertThat(databaseException.getMessage(), is("Error in database: User can not be created"));
        }
    }

    @Test
    void testView_givenCorrectUserId_whenExistUser_thenReturnTheUser() {
        var expectedUser = Optional.of(new User());
        var userH2RepositorySpy = spy(userH2Repository);
        doReturn(expectedUser).when(userH2RepositorySpy).findByIdOptional(any(UUID.class));

        var responseUser = userH2RepositorySpy.view(UUID.randomUUID());
        assertThat(expectedUser.get(), is(responseUser.get()));
    }

    @Test
    void testView_givenCorrectUserId_whenNotExistUser_thenReturnNull() {
        var userH2RepositorySpy = spy(userH2Repository);
        doReturn(Optional.empty()).when(userH2RepositorySpy).findByIdOptional(any(UUID.class));

        var responseUser = userH2RepositorySpy.view(UUID.randomUUID());
        assertThat(responseUser.isEmpty(), is(true));
    }

    @Test
    void testView_givenCorrectUserId_whenTheDatabaseCrash_thenReturnException() {
        var userH2RepositorySpy = spy(userH2Repository);
        doThrow(new PersistenceException("error in view")).when(userH2RepositorySpy).findByIdOptional(any(UUID.class));

        try {
            userH2RepositorySpy.view(UUID.randomUUID());
        } catch (DatabaseException databaseException) {
            assertThat(databaseException.getMessage(), is("Error in database: User can not be retrieved"));
        }
    }

}

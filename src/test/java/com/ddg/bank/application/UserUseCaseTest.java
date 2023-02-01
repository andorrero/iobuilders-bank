package com.ddg.bank.application;

import com.ddg.bank.application.exception.NotFoundException;
import com.ddg.bank.application.exception.UnauthorizedException;
import com.ddg.bank.application.security.PasswordSecurity;
import com.ddg.bank.domain.entity.User;
import com.ddg.bank.domain.mapper.UserMapper;
import com.ddg.bank.domain.model.UserDTO;
import com.ddg.bank.infra.outputport.UserOutputPort;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.openMocks;

@QuarkusTest
class UserUseCaseTest {

    @InjectMock
    UserOutputPort userOutputPortMock;

    @InjectMock
    UserMapper userMapperMock;

    @InjectMock
    PasswordSecurity passwordSecurityMock;

    @InjectMocks
    UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testFind_givenUserAndPass_whenUserExists_thenReturnUserDTO() {
        var expectedUser = new User();
        expectedUser.setName("John Doe");
        expectedUser.setUsername("user");
        expectedUser.setPassword("1234");
        expectedUser.setUserId(UUID.randomUUID());

        var expectedUserDTO = UserDTO.builder()
                .userId(expectedUser.getUserId())
                .name(expectedUser.getName())
                .username(expectedUser.getUsername())
                .password(expectedUser.getPassword()).build();

        doReturn(expectedUser).when(userOutputPortMock).find(anyString(), anyString());
        doReturn(expectedUserDTO).when(userMapperMock).mapToDTO(any(User.class));
        doReturn("1234").when(passwordSecurityMock).encrypt(anyString());
        doReturn("1234").when(passwordSecurityMock).decrypt(anyString());

        var userDTOCreated = userUseCase.find("user", "1234");
        assertThat(expectedUserDTO, is(userDTOCreated));
    }

    @Test
    void testFind_givenUserAndPass_whenUserNotExists_thenReturnException() {
        doReturn(null).when(userOutputPortMock).find(anyString(), anyString());

        try {
            userUseCase.find("test", "test");
        } catch (UnauthorizedException unauthorizedException) {
            assertThat(unauthorizedException.getMessage(), is("Error in authentication: user and/or password incorrect"));
        }
    }

    @Test
    void testCreate_givenValidUserDTO_whenUserIsCreated_thenReturnUserDTOCreated() {
        var expectedUser = new User();
        expectedUser.setName("John Doe");
        expectedUser.setUsername("user");
        expectedUser.setPassword("1234");
        expectedUser.setUserId(UUID.randomUUID());

        var expectedUserDTO = UserDTO.builder()
                .userId(expectedUser.getUserId())
                .name(expectedUser.getName())
                .username(expectedUser.getUsername())
                .password(expectedUser.getPassword()).build();

        doReturn(expectedUser).when(userOutputPortMock).create(any(User.class));
        doReturn(expectedUser).when(userMapperMock).mapToEntity(any(UserDTO.class));
        doReturn(expectedUserDTO).when(userMapperMock).mapToDTO(any(User.class));
        doReturn("1234").when(passwordSecurityMock).encrypt(anyString());

        var userDTOCreated = userUseCase.create(expectedUserDTO);
        assertThat(expectedUserDTO, is(userDTOCreated));
    }

    @Test
    void testView_givenValidUserId_whenUserExist_thenReturnUserDTO() {
        var expectedUser = new User();
        expectedUser.setName("John Doe");
        expectedUser.setUsername("user");
        expectedUser.setPassword("test");
        expectedUser.setUserId(UUID.randomUUID());

        var expectedUserDTO = UserDTO.builder()
                .userId(expectedUser.getUserId())
                .name(expectedUser.getName())
                .username(expectedUser.getUsername())
                .password(expectedUser.getPassword()).build();

        doReturn(Optional.of(expectedUser)).when(userOutputPortMock).view(any(UUID.class));
        doReturn(expectedUserDTO).when(userMapperMock).mapToDTO(any(User.class));
        doReturn("test").when(passwordSecurityMock).decrypt(anyString());

        var userDTORetrieved = userUseCase.view(expectedUser.getUserId().toString());
        assertThat(expectedUserDTO, is(userDTORetrieved));
    }

    @Test
    void testView_givenValidUserId_whenUserNotExist_thenThrowsNotFoundException() {
        doReturn(Optional.empty()).when(userOutputPortMock).view(any(UUID.class));

        try {
            userUseCase.view(UUID.randomUUID().toString());
        } catch (NotFoundException notFoundException) {
            assertThat(notFoundException.getMessage(), is("Not exists any User with the given uuid"));
        }
    }

}

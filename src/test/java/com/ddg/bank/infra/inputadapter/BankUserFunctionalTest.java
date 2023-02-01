package com.ddg.bank.infra.inputadapter;

import com.ddg.bank.application.security.TokenGenerator;
import com.ddg.bank.domain.model.UserDTO;
import com.ddg.bank.infra.inputport.UserInputPort;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.openMocks;

@QuarkusTest
class BankUserFunctionalTest {

    @Inject
    BankUserRestResource bankUserRestResource;

    @InjectMock
    UserInputPort userInputPortMock;

    @InjectMock
    TokenGenerator tokenGeneratorMock;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testLogin_givenValidUserAndPass_whenUserCanBRetrieved_thenReturnValidToken() {
        doReturn(UserDTO.builder().build()).when(userInputPortMock).find(anyString(), anyString());
        doReturn("token_valid").when(tokenGeneratorMock).generateToken(anyString());

        var response = bankUserRestResource.login("user", "pass");
        assertThat(Response.Status.OK.getStatusCode(), is(response.getStatus()));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ADMIN"})
    void testCreateUser_givenValidUser_whenUserCanBeCreated_thenReturnResponseCreated() {
        var requestUserDTO = UserDTO.builder().userId(UUID.randomUUID()).name("John Dee")
                .username("username").password("1234").build();
        doReturn(requestUserDTO).when(userInputPortMock).create(requestUserDTO);

        var response = bankUserRestResource.create(requestUserDTO);
        var responseUserDTO = response.readEntity(UserDTO.class);

        assertThat(Response.Status.CREATED.getStatusCode(), is(response.getStatus()));
        assertThat(response.getHeaderString(HttpHeaders.LOCATION), is("/bank/user/" + responseUserDTO.getUserId().toString()));
        assertThat(requestUserDTO, is(responseUserDTO));
    }

    @Test
    @TestSecurity(user = "admin", roles = {"ADMIN"})
    void testViewUser_givenValidUserId_whenUserCanBeRetrieved_thenReturnResponseWithUser() {
        var requestUserDTO = UserDTO.builder().userId(UUID.randomUUID()).name("John Dee")
                .username("username").password("1234").build();
        doReturn(requestUserDTO).when(userInputPortMock).view(any(String.class));

        var response = bankUserRestResource.view(UUID.randomUUID().toString());
        var responseUserDTO = response.readEntity(UserDTO.class);

        assertThat(Response.Status.OK.getStatusCode(), is(response.getStatus()));
        assertThat(requestUserDTO, is(responseUserDTO));
    }

}

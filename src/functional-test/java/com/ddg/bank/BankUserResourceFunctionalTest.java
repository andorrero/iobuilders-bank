package com.ddg.bank;

import com.ddg.bank.application.security.TokenGenerator;
import com.ddg.bank.domain.model.UserDTO;
import com.ddg.bank.infra.inputadapter.BankUserRestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.Status.UNSUPPORTED_MEDIA_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestHTTPEndpoint(BankUserRestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankUserResourceFunctionalTest {

    @Inject
    TokenGenerator tokenGenerator;

    private String tokenForAdminTest;

    private String tokenForUserTest;

    private UUID userIdForTest;

    @BeforeAll
    void loginUser() {
        tokenForAdminTest = tokenGenerator.generateToken("admin");
    }

    @Order(1)
    @Test
    void testLogin_givenUsernameMissing_whenDoLogin_thenReturnBadRequest() {
        given().when()
                .queryParam("password", "secret")
                .get("/login")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(2)
    @Test
    void testLogin_givenPasswordMissing_whenDoLogin_thenReturnBadRequest() {
        given().when()
                .queryParam("username", "user")
                .get("/login")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(3)
    @Test
    void testLogin_givenCorrectQueryParams_whenNoExistsUser_thenReturnNotFound() {
        var response = given().when()
                .queryParam("username", "user")
                .queryParam("password", "secret")
                .get("/login")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode())
                .extract().response();
        assertThat("Error in authentication: user and/or password incorrect", is(response.getBody().asString()));
    }

    @Order(4)
    @Test
    void testLogin_givenCorrectQueryParams_whenLoginForAdminUser_thenReturnToken() {
        var token = given().when()
                .queryParam("username", "admin")
                .queryParam("password", "secret")
                .get("/login")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response().asString();
        String jsonToken = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        assertThat(jsonToken.contains("admin"), is(true));
    }

    @Order(5)
    @Test
    void testCreateUser_givenNullUserDTO_whenOnCreateUser_thenReturnUnsupportedMediaType() {
        given().when()
                .post("/user")
                .then()
                .statusCode(UNSUPPORTED_MEDIA_TYPE.getStatusCode());
    }

    @Order(6)
    @Test
    void testCreateUser_givenEmptyToken_whenOnCreateUser_thenReturnUnauthorized() {
        var userDTO = UserDTO.builder().build();
        given().when()
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .post("/user")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(7)
    @Test
    void testCreateUser_givenEmptyUserDTO_whenOnCreateUser_thenReturnBadRequest() {
        var userDTO = UserDTO.builder().build();

        given().when()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .post("/user")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(8)
    @Test
    void testCreateUser_givenEmptyName_whenOnCreateUser_thenReturnBadRequest() {
        var userDTO = UserDTO.builder().build();
        userDTO.setUsername("username");
        userDTO.setPassword("password");

        given().when()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .post("/user")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(9)
    @Test
    void testCreateUser_givenEmptyUsername_whenOnCreateUser_thenReturnBadRequest() {
        var userDTO = UserDTO.builder().build();
        userDTO.setName("name");
        userDTO.setPassword("password");

        given().when()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .post("/user")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(10)
    @Test
    void testCreateUser_givenEmptyPassword_whenOnCreateUser_thenReturnBadRequest() {
        var userDTO = UserDTO.builder().build();
        userDTO.setUsername("username");
        userDTO.setName("name");

        given().when()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .post("/user")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(11)
    @Test
    void testCreateUser_givenCorrectUserDTO_whenOnCreateUser_thenReturnUserCreated() {
        var userDTO = UserDTO.builder().build();
        userDTO.setName("name");
        userDTO.setUsername("username");
        userDTO.setPassword("secret");

        var response = given()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .when().post("/user")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response();

        var responseUserDTO = response.getBody().as(UserDTO.class);
        assertThat(responseUserDTO.getUserId(), notNullValue());
        assertThat(responseUserDTO.getName(), is(userDTO.getName()));
        assertThat(responseUserDTO.getUsername(), is(userDTO.getUsername()));
        assertThat(responseUserDTO.getPassword(), is(userDTO.getPassword()));
        assertThat(response.getHeader("Location").contains("/bank/user/" + responseUserDTO.getUserId().toString()), is(true));

        userIdForTest = responseUserDTO.getUserId();
    }

    @Order(12)
    @Test
    void testCreateUser_givenCorrectUserDTO_whenExistsUserByUsername_thenReturnNoCreatedMessage() {
        var userDTO = UserDTO.builder().build();
        userDTO.setName("name");
        userDTO.setUsername("username");
        userDTO.setPassword("secret");

        var response = given()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .when().post("/user")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        assertThat("Impossible create user: username already exists", is(response.getBody().asString()));
    }

    @Order(13)
    @Test
    void testViewUser_givenEmptyToken_whenRetrieved_thenReturnUnauthorized() {
        given().contentType(MediaType.APPLICATION_JSON)
                .when().get("/user/" + UUID.randomUUID())
                .then().statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(14)
    @Test
    void testViewUser_givenWrongUserId_whenRetrieved_thenReturnBadRequest() {
        given()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON)
                .when().get("/user/1111")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(15)
    @Test
    void testViewUser_givenCorrectUserId_whenExistsUser_thenReturnUser() {
        var userDTO = UserDTO.builder().build();
        userDTO.setUserId(userIdForTest);
        userDTO.setName("name");
        userDTO.setUsername("username");
        userDTO.setPassword("secret");

        var retrievedUser = given()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON)
                .when().get("/user/" + userIdForTest)
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(UserDTO.class);

        assertThat(userDTO, is(retrievedUser));
    }

    @Order(16)
    @Test
    void testViewUser_givenCorrectUserId_whenNotExistsUser_thenReturnNotFound() {
        given()
                .auth().oauth2(tokenForAdminTest)
                .contentType(MediaType.APPLICATION_JSON)
                .when().get("/user/" + UUID.randomUUID())
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Order(17)
    @Test
    void testLoginUser_givenCorrectParams_whenUserExists_thenReturnTokenForUserRole() {
        var token = given().when()
                .queryParam("username", "username")
                .queryParam("password", "secret")
                .get("/login")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response().asString();

        String jsonToken = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        assertThat(jsonToken.contains("name"), is(true));

        tokenForUserTest = jsonToken;
    }

    @Order(18)
    @Test
    void testViewUser_givenUserRoleToken_whenRetrieveUser_thenReturnUnauthorized() {
        given()
                .auth().oauth2(tokenForUserTest)
                .contentType(MediaType.APPLICATION_JSON)
                .when().get("/user/" + UUID.randomUUID())
                .then().statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(19)
    @Test
    void testCreteUser_givenUserRoleToken_whenCreateUser_thenReturnUnauthorized() {
        var userDTO = UserDTO.builder().build();
        userDTO.setName("john");
        userDTO.setUsername("jdoe");
        userDTO.setPassword("secret");

        given()
                .auth().oauth2(tokenForUserTest)
                .contentType(MediaType.APPLICATION_JSON).body(userDTO)
                .when().post("/user")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

}

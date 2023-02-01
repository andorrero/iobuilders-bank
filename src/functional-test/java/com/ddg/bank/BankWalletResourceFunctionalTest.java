package com.ddg.bank;

import com.ddg.bank.application.security.TokenGenerator;
import com.ddg.bank.domain.model.WalletDTO;
import com.ddg.bank.domain.model.WalletMovementDTO;
import com.ddg.bank.domain.model.WalletTransferenceDTO;
import com.ddg.bank.infra.inputadapter.BankWalletRestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.Status.UNSUPPORTED_MEDIA_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestHTTPEndpoint(BankWalletRestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankWalletResourceFunctionalTest {

    @Inject
    TokenGenerator tokenGenerator;

    private String tokenForTest;

    private UUID walletIdForTest;

    private UUID walletIdForTransferenceTest;

    @BeforeAll
    void loginUser() {
        tokenForTest = tokenGenerator.generateToken("test");
    }

    @Order(1)
    @Test
    void testCreateWallet_givenNullWalletDTO_whenOnCreateWallet_thenReturnUnsupportedMediaType() {
        given().when()
                .post("/wallet")
                .then()
                .statusCode(UNSUPPORTED_MEDIA_TYPE.getStatusCode());
    }

    @Order(2)
    @Test
    void testCreateWallet_givenTokenEmpty_whenOnCreateWallet_thenReturnUnauthorized() {
        var walletDTO = WalletDTO.builder().build();

        given().when()
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(3)
    @Test
    void testCreateWallet_givenEmptyWalletDTO_whenOnCreateWallet_thenReturnBadRequest() {
        var walletDTO = WalletDTO.builder().build();

        given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(4)
    @Test
    void testCreateWallet_givenIncorrectOwnerId_whenOnCreateWallet_thenReturnBadRequest() {
        var walletDTO = WalletDTO.builder()
                .iban("ES1234123412341234")
                .currency("EUR").build();

        given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(5)
    @Test
    void testCreateWallet_givenIncorrectIBAN_whenOnCreateWallet_thenReturnBadRequest() {
        var walletDTO = WalletDTO.builder()
                .ownerId(UUID.randomUUID())
                .iban("ES1234")
                .currency("EUR").build();

        given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(6)
    @Test
    void testCreateWallet_givenEmptyCurrency_whenOnCreateWallet_thenReturnBadRequest() {
        var walletDTO = WalletDTO.builder()
                .ownerId(UUID.randomUUID())
                .iban("ES1234123412341234")
                .build();

        given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(7)
    @Test
    void testCreateWallet_givenCorrectWalletDTO_whenOnCreateWallet_thenReturnCreated() {
        var walletDTO = WalletDTO.builder()
                .ownerId(UUID.randomUUID())
                .iban("ES1234123412341234")
                .currency("EUR")
                .amount(100f)
                .build();

        var response = given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response();

        var responseWalletDTO = response.getBody().as(WalletDTO.class);
        assertThat(responseWalletDTO.getWalletId(), notNullValue());
        assertThat(responseWalletDTO.getAmount(), is(walletDTO.getAmount()));
        assertThat(responseWalletDTO.getCurrency(), is(walletDTO.getCurrency()));
        assertThat(responseWalletDTO.getIban(), is(walletDTO.getIban()));
        assertThat(responseWalletDTO.getOwnerId(), is(walletDTO.getOwnerId()));
        assertThat(response.getHeader("Location").contains("/bank/wallet/" + responseWalletDTO.getWalletId().toString()), is(true));

        walletIdForTest = responseWalletDTO.getWalletId();
    }

    @Order(8)
    @Test
    void testCreateWallet_givenCorrectWalletDTO_whenExistsWalletByIban_thenReturnNoCreatedMessage() {
        var walletDTO = WalletDTO.builder()
                .ownerId(UUID.randomUUID())
                .iban("ES1234123412341234")
                .currency("EUR")
                .amount(100f)
                .build();

        var response = given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        assertThat("Impossible create wallet: iban already exists", is(response.getBody().asString()));
    }

    @Order(9)
    @Test
    void testViewWallet_givenTokenEmpty_whenRetrieved_thenReturnBadRequest() {
        given().contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/1111")
                .then().statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(10)
    @Test
    void testViewWallet_givenWrongWalletId_whenRetrieved_thenReturnBadRequest() {
        given().auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/1111")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(11)
    @Test
    void testViewWallet_givenCorrectWallet_whenExists_thenReturnWallet() {
        var retrievedWallet = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/" + walletIdForTest)
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletDTO.class);

        assertThat(walletIdForTest, is(retrievedWallet.getWalletId()));
        assertThat(retrievedWallet.getOwnerId(), notNullValue());
        assertThat("EUR", is(retrievedWallet.getCurrency()));
        assertThat("ES1234123412341234", is(retrievedWallet.getIban()));
        assertThat(100f, is(retrievedWallet.getAmount()));
    }

    @Order(12)
    @Test
    void testViewWallet_givenCorrectWallet_whenNotExists_thenReturnNotFound() {
        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/" + UUID.randomUUID())
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Order(13)
    @Test
    void testDepositWallet_givenEmptyToken_whenCreateDeposit_thenReturnUnauthorized() {
        given().contentType(MediaType.APPLICATION_JSON)
                .when().post("/wallet/" + UUID.randomUUID() + "/deposit")
                .then().statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(14)
    @Test
    void testDepositWallet_givenInvalidWalletId_whenCreateDeposit_thenReturnBadRequest() {
        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().post("/wallet/1111/deposit")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(15)
    @Test
    void testDepositWallet_givenInvalidWalletMovementDTO_whenCreateDeposit_thenReturnBadRequest() {
        var walletMovementDTO = WalletMovementDTO.builder().build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletMovementDTO)
                .when().post("/wallet/" + walletIdForTest + "/deposit")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(16)
    @Test
    void testDepositWallet_givenInvalidAmount_whenCreateDeposit_thenReturnBadRequest() {
        var walletMovementDTO = WalletMovementDTO.builder()
                .concept("test").build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletMovementDTO)
                .when().post("/wallet/" + walletIdForTest + "/deposit")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(17)
    @Test
    void testDepositWallet_givenInvalidConcept_whenCreateDeposit_thenReturnBadRequest() {
        var walletMovementDTO = WalletMovementDTO.builder()
                .amount(10f).build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletMovementDTO)
                .when().post("/wallet/" + walletIdForTest + "/deposit")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(18)
    @Test
    void testDepositWallet_givenCorrectMovement_whenCreateDeposit_thenReturnCreated() {
        var walletMovementDTO = WalletMovementDTO.builder()
                .concept("test")
                .amount(100f).build();

        var responseMovement = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletMovementDTO)
                .when().post("/wallet/" + walletIdForTest + "/deposit")
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletMovementDTO.class);

        assertThat(responseMovement.getMovementId(), notNullValue());
        assertThat(responseMovement.getAmount(), is(walletMovementDTO.getAmount()));
        assertThat(responseMovement.getConcept(), is(walletMovementDTO.getConcept()));
        assertThat(responseMovement.getMovementDate(), notNullValue());
    }

    @Order(19)
    @Test
    void testViewWallet_givenCorrectWallet_whenExists_thenReturnWalletWithAmountUpdatedAndMovement() {
        var retrievedWallet = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/" + walletIdForTest)
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletDTO.class);

        assertThat(200f, is(retrievedWallet.getAmount()));
        assertThat(1, is(retrievedWallet.getMovements().size()));
    }

    @Order(20)
    @Test
    void testDepositWallet_givenCorrectMovement_whenWalletNotExist_thenReturn() {
        var walletMovementDTO = WalletMovementDTO.builder()
                .concept("test")
                .amount(10f).build();
        var walletUuid = UUID.randomUUID();

        var response=given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletMovementDTO)
                .when().post("/wallet/" + walletUuid + "/deposit")
                .then().statusCode(NOT_FOUND.getStatusCode())
                .extract().response();

        assertThat(String.format("Not exists the Wallet to create movement with UUID: '%s'", walletUuid.toString()), is(response.getBody().asString()));
    }

    @Order(21)
    @Test
    void testDepositWallet_givenCorrectMovementWithNegativeAmount_whenCreateDeposit_thenReturnCreated() {
        var walletMovementDTO = WalletMovementDTO.builder()
                .concept("test")
                .amount(-50f).build();

        var responseMovement = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletMovementDTO)
                .when().post("/wallet/" + walletIdForTest + "/deposit")
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletMovementDTO.class);

        assertThat(responseMovement.getMovementId(), notNullValue());
        assertThat(responseMovement.getAmount(), is(walletMovementDTO.getAmount()));
        assertThat(responseMovement.getConcept(), is(walletMovementDTO.getConcept()));
        assertThat(responseMovement.getMovementDate(), notNullValue());
    }

    @Order(22)
    @Test
    void testViewWallet_givenCorrectWallet_whenExists_thenReturnWalletWithAmountUpdatedAndMovements() {
        var retrievedWallet = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/" + walletIdForTest)
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletDTO.class);

        assertThat(150f, is(retrievedWallet.getAmount()));
        assertThat(2, is(retrievedWallet.getMovements().size()));
    }

    @Order(23)
    @Test
    void testTransferenceWallet_givenEmptyToken_whenCreateTransference_thenReturnUnauthorized() {
        given().contentType(MediaType.APPLICATION_JSON)
                .when().post("/wallet/transference")
                .then().statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Order(24)
    @Test
    void testTransferenceWallet_givenInvalidWalletTransference_whenCreateTransference_thenReturnBadRequest() {
        var walletTransferenceDTO = WalletTransferenceDTO.builder().build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(25)
    @Test
    void testTransferenceWallet_givenInvalidWalletOriginId_whenCreateTransference_thenReturnBadRequest() {
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletDestId(UUID.randomUUID())
                .amount(10f)
                .concept("test")
                .build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(26)
    @Test
    void testTransferenceWallet_givenInvalidWalletDestId_whenCreateTransference_thenReturnBadRequest() {
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletOriginId(UUID.randomUUID())
                .amount(10f)
                .concept("test")
                .build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(27)
    @Test
    void testTransferenceWallet_givenInvalidAmount_whenCreateTransference_thenReturnBadRequest() {
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletOriginId(UUID.randomUUID())
                .walletDestId(UUID.randomUUID())
                .concept("test")
                .build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(28)
    @Test
    void testTransferenceWallet_givenInvalidWConcept_whenCreateTransference_thenReturnBadRequest() {
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletOriginId(UUID.randomUUID())
                .amount(10f)
                .walletDestId(UUID.randomUUID())
                .build();

        given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(BAD_REQUEST.getStatusCode());
    }

    @Order(29)
    @Test
    void testTransferenceWallet_givenCorrectTransference_whenNotExistWalletOrigin_thenReturnNotFound() {
        var walletOriginId = UUID.randomUUID();
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletOriginId(walletOriginId)
                .amount(10f)
                .walletDestId(walletIdForTest)
                .concept("test")
                .build();

        var response = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(NOT_FOUND.getStatusCode())
                .extract().response();

        assertThat(String.format("Not exists the Wallet to create movement with UUID: '%s'", walletOriginId.toString()), is(response.getBody().asString()));
    }

    @Order(30)
    @Test
    void testTransferenceWallet_givenCorrectTransference_whenNotExistWalletDest_thenReturnNotFound() {
        var walletDestId = UUID.randomUUID();
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletOriginId(walletIdForTest)
                .amount(10f)
                .walletDestId(walletDestId)
                .concept("test")
                .build();

        var response = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(NOT_FOUND.getStatusCode())
                .extract().response();

        assertThat(String.format("Not exists the Wallet to create movement with UUID: '%s'", walletDestId.toString()), is(response.getBody().asString()));
    }

    @Order(31)
    @Test
    void testCreateWallet_givenCorrectWalletDTO_whenOnCreateWalletForTransference_thenReturnCreated() {
        var walletDTO = WalletDTO.builder()
                .ownerId(UUID.randomUUID())
                .iban("ES1234123412344567")
                .currency("EUR")
                .amount(5000f)
                .build();

        var response = given().when()
                .auth().oauth2(tokenForTest)
                .contentType(MediaType.APPLICATION_JSON).body(walletDTO)
                .post("/wallet")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response();

        var responseWalletDTO = response.getBody().as(WalletDTO.class);
        assertThat(responseWalletDTO.getWalletId(), notNullValue());

        walletIdForTransferenceTest = responseWalletDTO.getWalletId();
    }

    @Order(32)
    @Test
    void testTransferenceWallet_givenCorrectTransference_whenIsPossibleCreated_thenReturnTransferenceCreated() {
        var walletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletOriginId(walletIdForTransferenceTest)
                .amount(1000f)
                .walletDestId(walletIdForTest)
                .concept("test")
                .build();

        var response = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON).body(walletTransferenceDTO)
                .when().post("/wallet/transference")
                .then().statusCode(OK.getStatusCode())
                .extract().response();

        assertThat(walletTransferenceDTO, is(response.getBody().as(WalletTransferenceDTO.class)));
    }

    @Order(33)
    @Test
    void testViewWallet_givenWalletForTest_whenTransferenceHasDone_thenReturnWalletWithAmountUpdatedAndMovements() {
        var retrievedWallet = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/" + walletIdForTest)
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletDTO.class);

        assertThat(1150f, is(retrievedWallet.getAmount()));
        assertThat(3, is(retrievedWallet.getMovements().size()));
    }

    @Order(34)
    @Test
    void testViewWallet_givenWalletForTestTransference_whenTransferenceHasDone_thenReturnWalletWithAmountUpdatedAndMovement() {
        var retrievedWallet = given()
                .auth().oauth2(tokenForTest).contentType(MediaType.APPLICATION_JSON)
                .when().get("/wallet/" + walletIdForTransferenceTest)
                .then().statusCode(OK.getStatusCode())
                .extract().response().as(WalletDTO.class);

        assertThat(4000f, is(retrievedWallet.getAmount()));
        assertThat(1, is(retrievedWallet.getMovements().size()));
    }

}

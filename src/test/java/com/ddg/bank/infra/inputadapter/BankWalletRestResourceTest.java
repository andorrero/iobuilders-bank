package com.ddg.bank.infra.inputadapter;

import com.ddg.bank.domain.model.WalletDTO;
import com.ddg.bank.domain.model.WalletMovementDTO;
import com.ddg.bank.domain.model.WalletTransferenceDTO;
import com.ddg.bank.infra.inputport.WalletInputPort;
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
class BankWalletRestResourceTest {

    @Inject
    BankWalletRestResource bankWalletRestResource;

    @InjectMock
    WalletInputPort walletInputPortMock;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    @TestSecurity(user = "test", roles = {"USER"})
    void testCreateWallet_givenValidWallet_whenWalletCanBeCreated_thenReturnResponseCreated() {
        var requestWalletDTO = WalletDTO.builder().walletId(UUID.randomUUID()).currency("EUR")
                .ownerId(UUID.randomUUID()).iban("ES1234567890123456").amount(0f).build();
        doReturn(requestWalletDTO).when(walletInputPortMock).create(requestWalletDTO);

        var response = bankWalletRestResource.create(requestWalletDTO);
        var responseWalletDTO = response.readEntity(WalletDTO.class);

        assertThat(Response.Status.CREATED.getStatusCode(), is(response.getStatus()));
        assertThat(response.getHeaderString(HttpHeaders.LOCATION), is("/bank/wallet/" + responseWalletDTO.getWalletId().toString()));
        assertThat(requestWalletDTO, is(responseWalletDTO));
    }

    @Test
    @TestSecurity(user = "test", roles = {"USER"})
    void testViewWallet_givenValidWalletId_whenWalletCanBeRetrieved_thenReturnResponseWithWallet() {
        var retrievedWalletDTO = WalletDTO.builder().walletId(UUID.randomUUID()).currency("EUR")
                .ownerId(UUID.randomUUID()).iban("ES1234567890123456").amount(0f).build();
        doReturn(retrievedWalletDTO).when(walletInputPortMock).view(any(String.class));

        var response = bankWalletRestResource.view(UUID.randomUUID().toString());
        var responseWalletDTO = response.readEntity(WalletDTO.class);

        assertThat(Response.Status.OK.getStatusCode(), is(response.getStatus()));
        assertThat(retrievedWalletDTO, is(responseWalletDTO));
    }

    @Test
    @TestSecurity(user = "test", roles = {"USER"})
    void testDeposit_givenValidWalletIdAndAmount_whenWalletCanBeRetrieved_thenReturnCorrectResponse() {
        var requestWalletMovementDTO = WalletMovementDTO.builder().amount(100f).concept("cash").build();
        doReturn(requestWalletMovementDTO).when(walletInputPortMock).deposit(anyString(), any(WalletMovementDTO.class));

        var response = bankWalletRestResource.deposit(UUID.randomUUID().toString(), requestWalletMovementDTO);
        var responseWalletDTO = response.readEntity(WalletMovementDTO.class);

        assertThat(Response.Status.OK.getStatusCode(), is(response.getStatus()));
        assertThat(requestWalletMovementDTO, is(responseWalletDTO));
    }

    @Test
    @TestSecurity(user = "test", roles = {"USER"})
    void testTransference_givenValidWalletTransference_whenWalletTransferenceCanBeCreated_thenReturnCorrectResponse() {
        var requestWalletTransferenceDTO = WalletTransferenceDTO.builder()
                .walletDestId(UUID.randomUUID())
                .walletOriginId(UUID.randomUUID())
                .amount(10f)
                .concept("credit")
                .build();
        doReturn(requestWalletTransferenceDTO).when(walletInputPortMock).transference(any(WalletTransferenceDTO.class));

        var response = bankWalletRestResource.transference(requestWalletTransferenceDTO);
        var responseWalletTransferenceDTO = response.readEntity(WalletTransferenceDTO.class);

        assertThat(Response.Status.OK.getStatusCode(), is(response.getStatus()));
        assertThat(requestWalletTransferenceDTO, is(responseWalletTransferenceDTO));
    }

}

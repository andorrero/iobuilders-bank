package com.ddg.bank.application;

import com.ddg.bank.application.exception.NotFoundException;
import com.ddg.bank.domain.entity.Wallet;
import com.ddg.bank.domain.entity.WalletMovement;
import com.ddg.bank.domain.mapper.WalletMapper;
import com.ddg.bank.domain.mapper.WalletMovementMapper;
import com.ddg.bank.domain.model.WalletDTO;
import com.ddg.bank.domain.model.WalletMovementDTO;
import com.ddg.bank.domain.model.WalletTransferenceDTO;
import com.ddg.bank.infra.outputport.WalletMovementOutputPort;
import com.ddg.bank.infra.outputport.WalletOutputPort;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.openMocks;

@QuarkusTest
class WalletUseCaseTest {

    @InjectMock
    WalletOutputPort walletOutputPortMock;

    @InjectMock
    WalletMapper walletMapperMock;

    @InjectMock
    WalletMovementOutputPort walletMovementOutputPortMock;

    @InjectMock
    WalletMovementMapper walletMovementMapperMock;

    @InjectMocks
    WalletUseCase walletUseCase;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testCreate_givenValidWalletDTO_whenWalletIsCreated_thenReturnWalletDTOCreated() {
        var expectedWallet = new Wallet();
        expectedWallet.setAmount(100f);
        expectedWallet.setCurrency("EUR");
        expectedWallet.setOwnerId(UUID.randomUUID());
        expectedWallet.setAmount(100f);
        expectedWallet.setIban("ES1234123412341234");
        expectedWallet.setWalletId(UUID.randomUUID());

        var expectedWalletDTO = WalletDTO.builder()
                .walletId(expectedWallet.getWalletId())
                .amount(expectedWallet.getAmount())
                .currency(expectedWallet.getCurrency())
                .iban(expectedWallet.getIban())
                .ownerId(expectedWallet.getOwnerId()).build();

        doReturn(expectedWallet).when(walletOutputPortMock).create(any(Wallet.class));
        doReturn(expectedWallet).when(walletMapperMock).mapToEntity(any(WalletDTO.class));
        doReturn(expectedWalletDTO).when(walletMapperMock).mapToDTO(any(Wallet.class));

        var walletDTOCreated = walletUseCase.create(expectedWalletDTO);
        assertThat(expectedWalletDTO, is(walletDTOCreated));
    }

    @Test
    void testView_givenValidWalletId_whenWalletExist_thenReturnWalletDTO() {
        var expectedWallet = new Wallet();
        expectedWallet.setAmount(100f);
        expectedWallet.setCurrency("EUR");
        expectedWallet.setOwnerId(UUID.randomUUID());
        expectedWallet.setAmount(100f);
        expectedWallet.setIban("ES1234123412341234");
        expectedWallet.setWalletId(UUID.randomUUID());

        var expectedWalletDTO = WalletDTO.builder()
                .walletId(expectedWallet.getWalletId())
                .amount(expectedWallet.getAmount())
                .currency(expectedWallet.getCurrency())
                .iban(expectedWallet.getIban())
                .ownerId(expectedWallet.getOwnerId()).build();

        doReturn(Optional.of(expectedWallet)).when(walletOutputPortMock).view(any(UUID.class));
        doReturn(expectedWalletDTO).when(walletMapperMock).mapToDTO(any(Wallet.class));

        var walletDTORetrieved = walletUseCase.view(expectedWallet.getWalletId().toString());
        assertThat(expectedWalletDTO, is(walletDTORetrieved));
    }

    @Test
    void testView_givenValidWalletId_whenWalletNotExist_thenThrowsNotFoundException() {
        doReturn(Optional.empty()).when(walletOutputPortMock).view(any(UUID.class));

        try {
            walletUseCase.view(UUID.randomUUID().toString());
        } catch (NotFoundException notFoundException) {
            assertThat(notFoundException.getMessage(), is("Not exists any Wallet with the given uuid"));
        }
    }

    @Test
    void testDeposit_givenValidWalletMovement_whenWalletNotExist_thenReturnNotFoundException() {
        doReturn(Optional.empty()).when(walletOutputPortMock).view(any(UUID.class));
        var walletUUID = UUID.randomUUID();

        try {
            walletUseCase.deposit(walletUUID.toString(), WalletMovementDTO.builder().build());
        } catch (NotFoundException notFoundException) {
            assertThat(notFoundException.getMessage(), is(String.format("Not exists the Wallet to create movement with UUID: '%s'", walletUUID)));
        }
    }

    @Test
    void testDeposit_givenValidWalletMovement_whenWalletMovementIsCreated_thenReturnWalletMovementDTO() {
        var expectedWallet = new Wallet();
        expectedWallet.setAmount(100f);
        expectedWallet.setCurrency("EUR");
        expectedWallet.setOwnerId(UUID.randomUUID());
        expectedWallet.setAmount(100f);
        expectedWallet.setIban("ES1234123412341234");
        expectedWallet.setWalletId(UUID.randomUUID());
        doReturn(Optional.of(expectedWallet)).when(walletOutputPortMock).view(any(UUID.class));

        var expectedWalletMovement = new WalletMovement();
        expectedWalletMovement.setAmount(100f);
        expectedWalletMovement.setConcept("cash");
        expectedWalletMovement.setMovementId(UUID.randomUUID());
        expectedWalletMovement.setWalletId(UUID.randomUUID());
        expectedWalletMovement.setMovementDate(Instant.now());

        var expectedWalletMovementDTO = WalletMovementDTO.builder()
                .concept(expectedWalletMovement.getConcept())
                .movementId(expectedWalletMovement.getMovementId())
                .amount(expectedWalletMovement.getAmount())
                .movementDate(expectedWalletMovement.getMovementDate()).build();

        doReturn(expectedWalletMovement).when(walletMovementMapperMock).mapToEntity(any(WalletMovementDTO.class));
        doReturn(expectedWalletMovement).when(walletMovementOutputPortMock).create(any(WalletMovement.class));
        doReturn(expectedWalletMovementDTO).when(walletMovementMapperMock).mapToDTO(any(WalletMovement.class));
        doReturn(expectedWallet).when(walletOutputPortMock).update(expectedWallet);

        var walletMovementDTOCreated = walletUseCase.deposit(expectedWalletMovement.getWalletId().toString(), expectedWalletMovementDTO);
        assertThat(expectedWalletMovementDTO, is(walletMovementDTOCreated));
    }

    @Test
    void testTransference_givenValidWalletTransference_whenWalletMovementTransferenceAreCreated_thenReturnWalletTransferenceDTO() {
        var requestWalletTransferenceDTO = WalletTransferenceDTO.builder().walletOriginId(UUID.randomUUID())
                .walletDestId(UUID.randomUUID())
                .concept("test")
                .amount(10f)
                .build();

        doReturn(WalletMovementDTO.builder().build()).when(walletMovementMapperMock).mapToDTO(any(Float.class), anyString());

        var walletUseCaseSpy = spy(walletUseCase);
        doReturn(WalletMovementDTO.builder().build()).when(walletUseCaseSpy).deposit(anyString(), any(WalletMovementDTO.class));

        var responseWalletTransferenceDTO = walletUseCaseSpy.transference(requestWalletTransferenceDTO);
        assertThat(requestWalletTransferenceDTO, is(responseWalletTransferenceDTO));
    }

}

package com.ddg.bank.infra.outputadapter;

import com.ddg.bank.application.exception.DatabaseException;
import com.ddg.bank.application.exception.NonEmptyException;
import com.ddg.bank.domain.entity.User;
import com.ddg.bank.domain.entity.Wallet;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.openMocks;

@QuarkusTest
class WalletH2RepositoryTest {

    @InjectMocks
    WalletH2Repository walletH2Repository;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testCreate_givenCorrectWallet_whenIsPossibleToCreate_thenReturnWalletCreated() {
        var expectedWallet = new Wallet();
        var walletH2RepositorySpy = spy(walletH2Repository);
        doNothing().when(walletH2RepositorySpy).persistAndFlush(any(Wallet.class));

        var responseWallet = walletH2RepositorySpy.create(new Wallet());
        assertThat(expectedWallet, is(responseWallet));
    }

    @Test
    void testCreate_givenCorrectWallet_whenWalletExists_thenReturnException() {
        var expectedWallet = new Wallet();
        expectedWallet.setIban("ES1234123412341234");

        var walletH2RepositorySpy = spy(walletH2Repository);
        var panacheQueryMock = mock(PanacheQuery.class);
        doReturn(expectedWallet).when(panacheQueryMock).firstResult();
        doReturn(panacheQueryMock).when(walletH2RepositorySpy).find(anyString(), any(Parameters.class));

        try {
            walletH2RepositorySpy.create(expectedWallet);
        } catch (NonEmptyException nonEmptyException) {
            assertThat(nonEmptyException.getMessage(), is("Impossible create wallet: iban already exists"));
        }
    }

    @Test
    void testCreate_givenCorrectWallet_whenTheDatabaseCrash_thenReturnWalletException() {
        var walletH2RepositorySpy = spy(walletH2Repository);
        doThrow(new PersistenceException("error in creation")).when(walletH2RepositorySpy).persistAndFlush(any(Wallet.class));

        try {
            walletH2RepositorySpy.create(new Wallet());
        } catch (DatabaseException databaseException) {
            assertThat(databaseException.getMessage(), is("Error in database: Wallet can not be created"));
        }
    }

    @Test
    void testView_givenCorrectWalletId_whenExistWallet_thenReturnTheWallet() {
        var expectedWallet = Optional.of(new Wallet());
        var walletH2RepositorySpy = spy(walletH2Repository);
        doReturn(expectedWallet).when(walletH2RepositorySpy).findByIdOptional(any(UUID.class));

        var responseWallet = walletH2RepositorySpy.view(UUID.randomUUID());
        assertThat(expectedWallet.get(), is(responseWallet.get()));
    }

    @Test
    void testView_givenCorrectWalletId_whenNotExistWallet_thenReturnNull() {
        var walletH2RepositorySpy = spy(walletH2Repository);
        doReturn(Optional.empty()).when(walletH2RepositorySpy).findByIdOptional(any(UUID.class));

        var responseWallet = walletH2RepositorySpy.view(UUID.randomUUID());
        assertThat(responseWallet.isEmpty(), is(true));
    }

    @Test
    void testUpdate_givenCorrectWallet_whenIsPossibleToUpdate_thenReturnUpdatedWallet() {
        var expectedWallet = new Wallet();
        var walletH2RepositorySpy = spy(walletH2Repository);
        doNothing().when(walletH2RepositorySpy).persistAndFlush(any(Wallet.class));

        var responseWallet = walletH2RepositorySpy.update(new Wallet());
        assertThat(expectedWallet, is(responseWallet));
    }

    @Test
    void testUpdate_givenCorrectWallet_whenTheDatabaseCrash_thenReturnWalletException() {
        var walletH2RepositorySpy = spy(walletH2Repository);
        doThrow(new PersistenceException("error on updating")).when(walletH2RepositorySpy).persistAndFlush(any(Wallet.class));

        try {
            walletH2RepositorySpy.update(new Wallet());
        } catch (DatabaseException databaseException) {
            assertThat(databaseException.getMessage(), is("Error in database: Wallet can not be updated"));
        }
    }

}

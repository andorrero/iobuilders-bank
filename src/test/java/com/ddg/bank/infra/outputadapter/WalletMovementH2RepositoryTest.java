package com.ddg.bank.infra.outputadapter;

import com.ddg.bank.application.exception.DatabaseException;
import com.ddg.bank.domain.entity.WalletMovement;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.persistence.PersistenceException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.openMocks;

@QuarkusTest
class WalletMovementH2RepositoryTest {

    @InjectMocks
    WalletMovementH2Repository walletMovementH2Repository;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testCreate_givenCorrectWalletMovement_whenIsPossibleToCreate_thenReturnWalletMovementCreated() {
        var expectedWalletMovement = new WalletMovement();
        var walletMovementH2RepositorySpy = spy(walletMovementH2Repository);
        doNothing().when(walletMovementH2RepositorySpy).persistAndFlush(any(WalletMovement.class));

        var responseWalletMovement = walletMovementH2RepositorySpy.create(new WalletMovement());
        assertThat(expectedWalletMovement, is(responseWalletMovement));
    }

    @Test
    void testCreate_givenCorrectWalletMovement_whenTheDatabaseCrash_thenReturnException() {
        var walletMovementH2RepositorySpy = spy(walletMovementH2Repository);
        doThrow(new PersistenceException("error in creation")).when(walletMovementH2RepositorySpy).persistAndFlush(any(WalletMovement.class));

        try {
            walletMovementH2RepositorySpy.create(new WalletMovement());
        } catch (DatabaseException databaseException) {
            assertThat(databaseException.getMessage(), is("Error in database: Movement for Wallet can not be created"));
        }
    }
}

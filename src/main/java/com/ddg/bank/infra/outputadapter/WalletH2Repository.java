package com.ddg.bank.infra.outputadapter;

import com.ddg.bank.application.exception.DatabaseException;
import com.ddg.bank.application.exception.NonEmptyException;
import com.ddg.bank.domain.entity.Wallet;
import com.ddg.bank.infra.outputport.WalletOutputPort;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class WalletH2Repository implements WalletOutputPort, PanacheRepositoryBase<Wallet, UUID> {

    @Override
    @Transactional
    public Wallet create(final Wallet wallet) {
        try {
            var walletRetrieved = find("iban = :iban",
                    Parameters.with("iban", wallet.getIban()))
                    .firstResult();
            if (walletRetrieved != null) {
                throw new NonEmptyException("Impossible create wallet: iban already exists");
            }

            persistAndFlush(wallet);
            return wallet;
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: Wallet can not be created");
        }
    }

    @Override
    public Optional<Wallet> view(UUID walletId) {
        try {
            return findByIdOptional(walletId);
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: Wallet can not be retrieved");
        }
    }

    @Override
    public Wallet update(Wallet wallet) {
        try {
            persistAndFlush(wallet);
            return wallet;
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: Wallet can not be updated");
        }
    }

}

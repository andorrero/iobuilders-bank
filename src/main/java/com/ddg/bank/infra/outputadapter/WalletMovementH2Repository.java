package com.ddg.bank.infra.outputadapter;

import com.ddg.bank.application.exception.DatabaseException;
import com.ddg.bank.domain.entity.WalletMovement;
import com.ddg.bank.infra.outputport.WalletMovementOutputPort;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;
import java.util.UUID;

@ApplicationScoped
public class WalletMovementH2Repository implements WalletMovementOutputPort, PanacheRepositoryBase<WalletMovement, UUID> {

    @Override
    public WalletMovement create(final WalletMovement walletMovement) {
        try {
            persistAndFlush(walletMovement);
            return walletMovement;
        } catch (PersistenceException persistenceException) {
            throw new DatabaseException(persistenceException, "Error in database: Movement for Wallet can not be created");
        }
    }

}

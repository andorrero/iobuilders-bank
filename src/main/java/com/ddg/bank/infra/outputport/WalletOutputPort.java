package com.ddg.bank.infra.outputport;

import com.ddg.bank.domain.entity.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletOutputPort {

    Wallet create(final Wallet wallet);

    Optional<Wallet> view(final UUID walletId);

    Wallet update(final Wallet wallet);

}

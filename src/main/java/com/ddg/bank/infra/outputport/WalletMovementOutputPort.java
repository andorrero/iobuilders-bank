package com.ddg.bank.infra.outputport;

import com.ddg.bank.domain.entity.WalletMovement;

public interface WalletMovementOutputPort {

    WalletMovement create(final WalletMovement walletMovement);

}

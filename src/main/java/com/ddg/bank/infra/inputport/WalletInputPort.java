package com.ddg.bank.infra.inputport;

import com.ddg.bank.domain.model.WalletDTO;
import com.ddg.bank.domain.model.WalletMovementDTO;
import com.ddg.bank.domain.model.WalletTransferenceDTO;
import com.ddg.bank.infra.inputport.validation.UUID;

public interface WalletInputPort {

    WalletDTO create(final WalletDTO walletDTO);

    WalletDTO view(@UUID String walletId);

    WalletMovementDTO deposit(@UUID String walletId, WalletMovementDTO walletMovementDTO);

    WalletTransferenceDTO transference(WalletTransferenceDTO walletTransferenceDTO);

}

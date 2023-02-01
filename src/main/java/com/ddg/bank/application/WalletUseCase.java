package com.ddg.bank.application;

import com.ddg.bank.application.exception.NotFoundException;
import com.ddg.bank.domain.mapper.WalletMapper;
import com.ddg.bank.domain.mapper.WalletMovementMapper;
import com.ddg.bank.domain.model.WalletDTO;
import com.ddg.bank.domain.model.WalletMovementDTO;
import com.ddg.bank.domain.model.WalletTransferenceDTO;
import com.ddg.bank.infra.inputport.WalletInputPort;
import com.ddg.bank.infra.outputport.WalletMovementOutputPort;
import com.ddg.bank.infra.outputport.WalletOutputPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class WalletUseCase implements WalletInputPort {

    @Inject
    WalletOutputPort walletOutputPort;

    @Inject
    WalletMovementOutputPort walletMovementOutputPort;

    @Inject
    WalletMapper walletMapper;

    @Inject
    WalletMovementMapper walletMovementMapper;

    @Override
    public WalletDTO create(WalletDTO walletDTO) {
        return walletMapper.mapToDTO(walletOutputPort.create(walletMapper.mapToEntity(walletDTO)));
    }

    @Override
    public WalletDTO view(String walletId) {
        var optionalWallet = walletOutputPort.view(UUID.fromString(walletId));
        if (optionalWallet.isEmpty()) {
            throw new NotFoundException("Not exists any Wallet with the given uuid");
        }
        return walletMapper.mapToDTO(optionalWallet.get());
    }

    @Override
    @Transactional
    public WalletMovementDTO deposit(String walletId, WalletMovementDTO walletMovementDTO) {
        var optionalWalletStored = walletOutputPort.view(UUID.fromString(walletId));
        if (optionalWalletStored.isEmpty()) {
            throw new NotFoundException(String.format("Not exists the Wallet to create movement with UUID: '%s'", walletId));
        }
        var walletStored = optionalWalletStored.get();

        var walletMovementToStore = walletMovementMapper.mapToEntity(walletMovementDTO);
        walletMovementToStore.setWalletId(walletStored.getWalletId());
        var walletMovementStored = walletMovementOutputPort.create(walletMovementToStore);

        walletStored.setAmount(walletStored.getAmount() + walletMovementStored.getAmount());
        walletOutputPort.update(walletStored);

        return walletMovementMapper.mapToDTO(walletMovementStored);
    }

    @Override
    @Transactional
    public WalletTransferenceDTO transference(WalletTransferenceDTO walletTransferenceDTO) {
        var walletMovementOrigin = walletMovementMapper.mapToDTO(walletTransferenceDTO.getAmount() * -1, walletTransferenceDTO.getConcept());
        deposit(walletTransferenceDTO.getWalletOriginId().toString(), walletMovementOrigin);

        var walletMovementDest = walletMovementMapper.mapToDTO(walletTransferenceDTO.getAmount(), walletTransferenceDTO.getConcept());
        deposit(walletTransferenceDTO.getWalletDestId().toString(), walletMovementDest);

        return walletTransferenceDTO;
    }

}

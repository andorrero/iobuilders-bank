package com.ddg.bank.domain.mapper;

import com.ddg.bank.domain.entity.Wallet;
import com.ddg.bank.domain.model.WalletDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface WalletMapper {

    Wallet mapToEntity(WalletDTO walletDTO);

    WalletDTO mapToDTO(Wallet wallet);

}

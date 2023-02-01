package com.ddg.bank.domain.mapper;

import com.ddg.bank.domain.entity.WalletMovement;
import com.ddg.bank.domain.model.WalletMovementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface WalletMovementMapper {

    @Mapping(target = "movementDate", ignore = true)
    WalletMovement mapToEntity(WalletMovementDTO walletMovementDTO);

    WalletMovementDTO mapToDTO(WalletMovement walletMovement);

    default WalletMovementDTO mapToDTO(Float amount, String concept) {
        return WalletMovementDTO.builder()
                .amount(amount)
                .concept(concept)
                .build();
    }

}

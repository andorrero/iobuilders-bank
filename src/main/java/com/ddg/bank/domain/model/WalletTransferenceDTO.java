package com.ddg.bank.domain.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@RegisterForReflection
@EqualsAndHashCode
@Builder
@ToString
@Jacksonized
public class WalletTransferenceDTO {

    @NotNull(message = "required")
    private UUID walletOriginId;

    @NotNull(message = "required")
    private UUID walletDestId;

    @NotNull
    @Min(value = 0L, message = "positive")
    private Float amount;

    @NotEmpty(message = "required")
    @Size(max = 128, message = "max_size")
    private String concept;

}

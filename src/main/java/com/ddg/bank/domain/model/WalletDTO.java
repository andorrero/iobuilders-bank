package com.ddg.bank.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RegisterForReflection
@EqualsAndHashCode
@Builder
@ToString
@Jacksonized
public class WalletDTO {

    private UUID walletId;

    @NotNull(message = "required")
    private UUID ownerId;

    @NotEmpty(message = "required")
    @Size(min = 18, max = 18, message = "max_size")
    private String iban;

    @Builder.Default
    private Float amount = 0f;

    @NotEmpty(message = "required")
    @Size(min = 3, max = 3, message = "max_size")
    private String currency;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WalletMovementDTO> movements;

}
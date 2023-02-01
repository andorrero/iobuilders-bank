package com.ddg.bank.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@RegisterForReflection
@EqualsAndHashCode
@Builder
@ToString
@Jacksonized
public class WalletMovementDTO {

    private UUID movementId;

    @NotNull
    private Float amount;

    @NotEmpty(message = "required")
    @Size(max = 128, message = "max_size")
    private String concept;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant movementDate;

}

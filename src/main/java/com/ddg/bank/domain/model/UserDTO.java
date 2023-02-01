package com.ddg.bank.domain.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@RegisterForReflection
@EqualsAndHashCode
@Builder
@ToString
@Jacksonized
public class UserDTO {

    private UUID userId;

    @NotEmpty(message = "required")
    @Size(max = 128, message = "max_size")
    private String name;

    @NotEmpty(message = "required")
    @Size(max = 128, message = "max_size")
    private String username;

    @NotEmpty(message = "required")
    @Size(max = 128, message = "max_size")
    private String password;

}

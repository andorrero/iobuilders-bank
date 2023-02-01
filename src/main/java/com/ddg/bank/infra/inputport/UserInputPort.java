package com.ddg.bank.infra.inputport;

import com.ddg.bank.domain.model.UserDTO;
import com.ddg.bank.infra.inputport.validation.UUID;

import javax.validation.Valid;

public interface UserInputPort {

    UserDTO find(final String username, final String password);

    UserDTO create(@Valid UserDTO userDTO);

    UserDTO view(@UUID String userId);

}

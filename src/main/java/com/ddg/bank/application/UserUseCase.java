package com.ddg.bank.application;

import com.ddg.bank.application.exception.NotFoundException;
import com.ddg.bank.application.exception.UnauthorizedException;
import com.ddg.bank.application.security.PasswordSecurity;
import com.ddg.bank.domain.mapper.UserMapper;
import com.ddg.bank.domain.model.UserDTO;
import com.ddg.bank.infra.inputport.UserInputPort;
import com.ddg.bank.infra.outputport.UserOutputPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class UserUseCase implements UserInputPort {

    @Inject
    UserOutputPort userOutputPort;

    @Inject
    UserMapper userMapper;

    @Inject
    PasswordSecurity passwordSecurity;

    @Override
    public UserDTO find(String username, String password) {
        var user = userOutputPort.find(username, passwordSecurity.encrypt(password));
        if (user == null) {
            throw new UnauthorizedException("Error in authentication: user and/or password incorrect");
        }

        var userDTO = userMapper.mapToDTO(user);
        userDTO.setPassword(passwordSecurity.decrypt(userDTO.getPassword()));
        return userDTO;
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        var user = userMapper.mapToEntity(userDTO);
        user.setPassword(passwordSecurity.encrypt(user.getPassword()));

        var createdUserDTO = userMapper.mapToDTO(userOutputPort.create(user));
        createdUserDTO.setPassword(passwordSecurity.decrypt(createdUserDTO.getPassword()));
        return createdUserDTO;
    }

    @Override
    public UserDTO view(String userId) {
        var optionalUser = userOutputPort.view(UUID.fromString(userId));
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Not exists any User with the given uuid");
        }

        var userDTO = userMapper.mapToDTO(optionalUser.get());
        userDTO.setPassword(passwordSecurity.decrypt(userDTO.getPassword()));
        return userDTO;
    }

}

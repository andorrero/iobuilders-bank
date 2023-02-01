package com.ddg.bank.domain.mapper;

import com.ddg.bank.domain.entity.User;
import com.ddg.bank.domain.model.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    User mapToEntity(UserDTO userDTO);

    UserDTO mapToDTO(User user);

}

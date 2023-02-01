package com.ddg.bank.infra.outputport;

import com.ddg.bank.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserOutputPort {

    User find(final String username, final String password);

    User create(final User user);

    Optional<User> view(final UUID userId);

}

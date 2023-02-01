package com.ddg.bank.application.security;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class PasswordSecurityTest {

    @Inject
    PasswordSecurity passwordSecurity;

    @Test
    void givenPassword_whenEncryptAndDecryptThePassword_thenReturnSameValue() {
        var passEncrypted = passwordSecurity.encrypt("secret");
        var passDecrypt = passwordSecurity.decrypt(passEncrypted);
        assertThat("secret", is(passDecrypt));
    }

}

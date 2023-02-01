package com.ddg.bank.application.security;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class TokenGeneratorTest {

    @Inject
    TokenGenerator tokenGenerator;

    @Test
    void givenUserName_whenGenerateToken_thenReturnValidToken() {
        var generatedToken = tokenGenerator.generateToken("dani");
        String json = new String(Base64.getUrlDecoder().decode(generatedToken.split("\\.")[1]), StandardCharsets.UTF_8);
        assertThat(json.contains("dani"), is(true));
        assertThat(json.contains("USER"), is(true));
    }

    @Test
    void givenAdminUserName_whenGenerateToken_thenReturnValidToken() {
        var generatedToken = tokenGenerator.generateToken("admin");
        String json = new String(Base64.getUrlDecoder().decode(generatedToken.split("\\.")[1]), StandardCharsets.UTF_8);
        assertThat(json.contains("admin"), is(true));
        assertThat(json.contains("ADMIN"), is(true));
    }

}

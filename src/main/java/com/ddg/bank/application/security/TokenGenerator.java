package com.ddg.bank.application.security;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class TokenGenerator {

    public String generateToken(final String name) {
        return Jwt.issuer("https://example.com/issuer")
                .upn(name)
                .groups(new HashSet<>(generateRoles(name)))
                .claim(Claims.birthdate.name(), "2001-07-13")
                .sign();
    }

    private List<String> generateRoles(final String name) {
        if("admin".equals(name)) return Arrays.asList("USER", "ADMIN");
        return Arrays.asList("USER");
    }

}

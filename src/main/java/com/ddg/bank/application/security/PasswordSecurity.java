package com.ddg.bank.application.security;

import javax.enterprise.context.ApplicationScoped;
import java.util.Base64;

@ApplicationScoped
public class PasswordSecurity {

    public String encrypt(final String passwordToEncrypt) {
        return Base64.getEncoder().encodeToString(passwordToEncrypt.getBytes());
    }

    public String decrypt(final String passwordToDecrypt) {
        return new String(Base64.getDecoder().decode(passwordToDecrypt));
    }

}

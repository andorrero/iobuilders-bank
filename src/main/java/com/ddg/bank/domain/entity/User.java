package com.ddg.bank.domain.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity(name = "BANK_USER")
@Cacheable
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RegisterForReflection
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "USER_ID", updatable = false, columnDefinition = "uuid", length = 36)
    private UUID userId;

    @Column(name = "USER_NAME", length = 128)
    private String name;

    @Column(name = "USER_USERNAME", length = 128, unique = true)
    private String username;

    @Column(name = "USER_PASS", length = 128)
    private String password;

}

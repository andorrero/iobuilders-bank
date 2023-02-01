package com.ddg.bank.domain.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity(name = "BANK_WALLET_MOVEMENT")
@Cacheable
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RegisterForReflection
public class WalletMovement {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "MOVEMENT_ID", updatable = false, columnDefinition = "uuid", length = 36)
    private UUID movementId;

    @Column(name = "WALLET_ID", columnDefinition = "uuid", length = 36)
    private UUID walletId;

    @Column(name = "AMOUNT")
    private Float amount;

    @Column(name = "CONCEPT", length = 128)
    private String concept;

    @CreationTimestamp
    @Column(name = "MOVEMENT_DATE")
    private Instant movementDate;

}
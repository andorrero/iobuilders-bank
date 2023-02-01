package com.ddg.bank.domain.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.List;
import java.util.UUID;

@Entity(name = "BANK_WALLET")
@Cacheable
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RegisterForReflection
public class Wallet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "WALLET_ID", updatable = false, columnDefinition = "uuid", length = 36)
    private UUID walletId;

    @Column(name = "OWNER_ID", columnDefinition = "uuid", length = 36)
    private UUID ownerId;

    @Column(name = "IBAN", length = 18, unique = true)
    private String iban;

    @Column(name = "AMOUNT")
    private Float amount;

    @Column(name = "CURRENCY", length = 128)
    private String currency;

    @OneToMany(mappedBy = "walletId", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("movementDate ASC")
    private List<WalletMovement> movements;

}

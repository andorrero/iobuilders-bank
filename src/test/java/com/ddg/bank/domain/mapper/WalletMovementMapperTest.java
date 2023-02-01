package com.ddg.bank.domain.mapper;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class WalletMovementMapperTest {

    @Inject
    WalletMovementMapper walletMovementMapper;

    @Test
    void testMapToDTO_givenMovementFields_whenMapperToDTO_thenReturnWalletMovement() {
        var concept = "test";
        var amount = 10f;

        var walletMovementDTO = walletMovementMapper.mapToDTO(amount, concept);
        assertThat(concept, is(walletMovementDTO.getConcept()));
        assertThat(amount, is(walletMovementDTO.getAmount()));
    }

}

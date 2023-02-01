package com.ddg.bank.infra.inputport.validation;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UUIDTest {

    Validator validator;

    @BeforeAll
    public void setUp() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "57c4cc83-cc97-4167-a0ce-066b521d7dc6",
            "251771de-1751-43ea-8b4a-686fe669a6e7",
            "aaaaaaaa-4444-4444-4444-444444444444"
    })
    void givenUUID_whenIsValid_thenReturnNoValidationErrors(final String uuid) {
        assertThat(validator.validate(new RequestWithUUIDMock(uuid)).size(), is(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "57c4cc83-cc974167-a0ce-066b521d7dc6",
            "251771de -1751-43ea-8b4a-686fe669a6e7",
            "522268e9-zzzz-4cf3-80f9-24be8d1de7a1",
            "522268e9-zzzz-4cf3-80f9-24be8d1de7a1ffffff"
    })
    void givenUUID_whenIsNotValid_thenReturnValidationErrors(final String uuid) {
        assertThat(validator.validate(new RequestWithUUIDMock(uuid)).size(), is(1));
    }
}

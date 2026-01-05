package com.gp_dev.erp_lite.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("FrenchSiretValidator Tests")
class FrenchSiretValidatorTest {

    private FrenchSiretValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new FrenchSiretValidator();
        
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);
    }

    @Nested
    @DisplayName("Valid SIRET Numbers")
    class ValidSiretTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should accept null and empty values")
        void shouldAcceptNullAndEmptyValues(String siret) {
            assertTrue(validator.isValid(siret, context));
        }

        @Test
        @DisplayName("should accept valid SIRET with Luhn check")
        void shouldAcceptValidSiret() {
            // SIRET de la Mairie de Paris (exemple connu)
            // Note: Pour les tests, utilisons un SIRET qui passe l'algorithme de Luhn
            assertTrue(validator.isValid("73282932000074", context));
        }

        @Test
        @DisplayName("should accept SIRET with spaces")
        void shouldAcceptSiretWithSpaces() {
            assertTrue(validator.isValid("732 829 320 00074", context));
        }

        @Test
        @DisplayName("should accept SIRET with dashes")
        void shouldAcceptSiretWithDashes() {
            assertTrue(validator.isValid("732-829-320-00074", context));
        }
    }

    @Nested
    @DisplayName("Invalid SIRET Numbers")
    class InvalidSiretTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "1234567890123",      // 13 digits
            "123456789012345",    // 15 digits
            "1234567890"          // 10 digits
        })
        @DisplayName("should reject SIRET with incorrect length")
        void shouldRejectIncorrectLength(String siret) {
            assertFalse(validator.isValid(siret, context));
        }

        @Test
        @DisplayName("should reject SIRET with letters")
        void shouldRejectSiretWithLetters() {
            assertFalse(validator.isValid("1234567890123A", context));
        }

        @Test
        @DisplayName("should reject SIRET failing Luhn check")
        void shouldRejectInvalidLuhnCheck() {
            // SIRET qui ne passe pas l'algorithme de Luhn
            assertFalse(validator.isValid("12345678901234", context));
        }

        @Test
        @DisplayName("should reject all zeros (invalid SIRET)")
        void shouldRejectAllZeros() {
            assertFalse(validator.isValid("00000000000000", context));
        }
    }
}


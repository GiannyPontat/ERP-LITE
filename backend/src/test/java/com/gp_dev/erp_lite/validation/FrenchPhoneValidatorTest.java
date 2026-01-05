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

@DisplayName("FrenchPhoneValidator Tests")
class FrenchPhoneValidatorTest {

    private FrenchPhoneValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new FrenchPhoneValidator();
        
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);
    }

    @Nested
    @DisplayName("Valid French Phone Numbers")
    class ValidPhoneTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should accept null and empty values")
        void shouldAcceptNullAndEmptyValues(String phone) {
            assertTrue(validator.isValid(phone, context));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "0612345678",
            "0712345678",
            "0112345678",
            "0212345678",
            "0312345678",
            "0412345678",
            "0512345678",
            "0912345678"
        })
        @DisplayName("should accept valid national format (10 digits)")
        void shouldAcceptNationalFormat(String phone) {
            assertTrue(validator.isValid(phone, context));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "06 12 34 56 78",
            "06.12.34.56.78",
            "06-12-34-56-78"
        })
        @DisplayName("should accept national format with separators")
        void shouldAcceptNationalFormatWithSeparators(String phone) {
            assertTrue(validator.isValid(phone, context));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "+33612345678",
            "+33 6 12 34 56 78",
            "+33.6.12.34.56.78",
            "0033612345678",
            "0033 6 12 34 56 78"
        })
        @DisplayName("should accept international format")
        void shouldAcceptInternationalFormat(String phone) {
            assertTrue(validator.isValid(phone, context));
        }
    }

    @Nested
    @DisplayName("Invalid French Phone Numbers")
    class InvalidPhoneTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "061234567",      // 9 digits
            "06123456789",    // 11 digits (not international)
            "123456789"       // Missing leading 0
        })
        @DisplayName("should reject incorrect length")
        void shouldRejectIncorrectLength(String phone) {
            assertFalse(validator.isValid(phone, context));
        }

        @Test
        @DisplayName("should reject phone starting with 00")
        void shouldRejectStartingWithDoubleZero() {
            // 00 is only valid as international prefix (0033)
            assertFalse(validator.isValid("0012345678", context));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "1612345678",     // Starting with 1 (not valid in France)
            "2612345678",     // Starting with 2
            "8612345678"      // Starting with 8
        })
        @DisplayName("should reject invalid prefixes")
        void shouldRejectInvalidPrefixes(String phone) {
            assertFalse(validator.isValid(phone, context));
        }

        @Test
        @DisplayName("should reject phone with letters")
        void shouldRejectPhoneWithLetters() {
            assertFalse(validator.isValid("061234567A", context));
        }

        @Test
        @DisplayName("should reject completely invalid format")
        void shouldRejectInvalidFormat() {
            assertFalse(validator.isValid("phone123", context));
        }
    }
}


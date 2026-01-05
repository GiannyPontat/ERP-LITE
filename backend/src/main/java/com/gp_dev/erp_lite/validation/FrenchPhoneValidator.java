package com.gp_dev.erp_lite.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for French phone numbers.
 * 
 * Valid French phone formats:
 * - Fixed lines: 01-05 (mainland France)
 * - Mobile: 06, 07
 * - Non-geographic: 09
 * - Overseas territories: various codes
 * 
 * Accepts international format (+33 or 0033) or national format (0x)
 */
public class FrenchPhoneValidator implements ConstraintValidator<FrenchPhone, String> {

    // Pattern for French phone numbers
    // Accepts: 0XXXXXXXXX, +33XXXXXXXXX, 0033XXXXXXXXX
    // With optional separators (spaces, dots, dashes)
    private static final Pattern FRENCH_PHONE_PATTERN = Pattern.compile(
        "^(?:" +
            // International format with +33
            "(?:\\+33|0033)[\\s.-]?[1-9](?:[\\s.-]?\\d{2}){4}" +
            "|" +
            // National format starting with 0
            "0[1-9](?:[\\s.-]?\\d{2}){4}" +
        ")$"
    );

    // Pattern for the clean number (digits only)
    private static final Pattern CLEAN_NUMBER_PATTERN = Pattern.compile("^(?:33|0)?[1-9]\\d{8}$");

    @Override
    public void initialize(FrenchPhone constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        // Null or empty values are considered valid (use @NotBlank for required)
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }

        String trimmedPhone = phone.trim();

        // First, check the general format
        if (!FRENCH_PHONE_PATTERN.matcher(trimmedPhone).matches()) {
            // Try with clean number check
            String cleanNumber = trimmedPhone.replaceAll("[\\s.\\-+]", "");
            if (!CLEAN_NUMBER_PATTERN.matcher(cleanNumber).matches()) {
                setCustomMessage(context, "Format invalide. Utilisez: 06 12 34 56 78 ou +33 6 12 34 56 78");
                return false;
            }
        }

        // Extract digits only for additional validation
        String digits = trimmedPhone.replaceAll("[^\\d]", "");

        // Validate length: should be 10 digits (national) or 11-12 (international)
        if (digits.length() < 10 || digits.length() > 12) {
            setCustomMessage(context, "Le numéro doit contenir 10 chiffres");
            return false;
        }

        // Validate prefix
        if (!isValidFrenchPrefix(digits)) {
            setCustomMessage(context, "Le préfixe du numéro n'est pas valide pour la France");
            return false;
        }

        return true;
    }

    /**
     * Validates that the phone number starts with a valid French prefix.
     * Valid prefixes: 01-09 for national format, or 33 + 1-9 for international
     */
    private boolean isValidFrenchPrefix(String digits) {
        // International format starting with 33
        if (digits.startsWith("33")) {
            String afterPrefix = digits.substring(2);
            if (afterPrefix.length() >= 1) {
                char firstDigit = afterPrefix.charAt(0);
                // Valid first digits after +33: 1-9
                return firstDigit >= '1' && firstDigit <= '9';
            }
            return false;
        }

        // National format starting with 0
        if (digits.startsWith("0")) {
            if (digits.length() >= 2) {
                char secondDigit = digits.charAt(1);
                // Valid second digits: 1-9 (01-09)
                return secondDigit >= '1' && secondDigit <= '9';
            }
            return false;
        }

        return false;
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}


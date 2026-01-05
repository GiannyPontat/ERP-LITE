package com.gp_dev.erp_lite.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for French SIRET numbers using the Luhn algorithm.
 * 
 * A SIRET number consists of 14 digits:
 * - 9 digits for SIREN (company identifier)
 * - 5 digits for NIC (establishment identifier)
 * 
 * The sum of all digits (with even-positioned digits doubled and >9 reduced by 9)
 * must be divisible by 10.
 */
public class FrenchSiretValidator implements ConstraintValidator<FrenchSiret, String> {

    @Override
    public void initialize(FrenchSiret constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String siret, ConstraintValidatorContext context) {
        // Null or empty values are considered valid (use @NotBlank for required)
        if (siret == null || siret.trim().isEmpty()) {
            return true;
        }

        // Remove any spaces or dashes
        String cleanSiret = siret.replaceAll("[\\s-]", "");

        // Must be exactly 14 digits
        if (!cleanSiret.matches("^\\d{14}$")) {
            setCustomMessage(context, "Le SIRET doit contenir exactement 14 chiffres");
            return false;
        }

        // Apply Luhn algorithm
        if (!isValidLuhn(cleanSiret)) {
            setCustomMessage(context, "Le numéro SIRET ne passe pas la vérification de clé");
            return false;
        }

        return true;
    }

    /**
     * Validates a number using the Luhn algorithm.
     * 
     * Algorithm:
     * 1. Starting from the rightmost digit, double the value of every second digit
     * 2. If doubling results in a number > 9, subtract 9
     * 3. Sum all digits
     * 4. The sum must be divisible by 10
     */
    private boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;

        // Process from right to left
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}


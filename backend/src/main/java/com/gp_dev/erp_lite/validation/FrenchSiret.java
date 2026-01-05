package com.gp_dev.erp_lite.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for French SIRET numbers.
 * A valid SIRET must:
 * - Be exactly 14 digits
 * - Pass the Luhn algorithm check
 */
@Documented
@Constraint(validatedBy = FrenchSiretValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FrenchSiret {
    String message() default "Le numéro SIRET n'est pas valide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


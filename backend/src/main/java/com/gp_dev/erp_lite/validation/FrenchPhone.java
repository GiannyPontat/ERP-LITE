package com.gp_dev.erp_lite.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for French phone numbers.
 * Accepts formats:
 * - 0612345678
 * - 06 12 34 56 78
 * - 06.12.34.56.78
 * - 06-12-34-56-78
 * - +33612345678
 * - +33 6 12 34 56 78
 * - 0033612345678
 */
@Documented
@Constraint(validatedBy = FrenchPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FrenchPhone {
    String message() default "Le numéro de téléphone n'est pas un numéro français valide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


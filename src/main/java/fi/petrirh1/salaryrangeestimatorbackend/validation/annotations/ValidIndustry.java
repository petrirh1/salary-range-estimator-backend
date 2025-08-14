package fi.petrirh1.salaryrangeestimatorbackend.validation.annotations;

import fi.petrirh1.salaryrangeestimatorbackend.validation.validators.IndustryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IndustryValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIndustry {

    String message() default "Invalid industry";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

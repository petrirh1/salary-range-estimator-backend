package fi.petrirh1.salaryrangeestimatorbackend.validation.annotations;

import fi.petrirh1.salaryrangeestimatorbackend.validation.validators.EducationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EducationValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEducation {

    String message() default "Invalid education";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

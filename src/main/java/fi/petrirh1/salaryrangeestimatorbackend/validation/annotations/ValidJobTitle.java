package fi.petrirh1.salaryrangeestimatorbackend.validation.annotations;

import fi.petrirh1.salaryrangeestimatorbackend.validation.validators.JobTitleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JobTitleValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJobTitle {

    String message() default "Invalid job title";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

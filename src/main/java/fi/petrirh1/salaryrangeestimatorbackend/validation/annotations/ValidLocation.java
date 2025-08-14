package fi.petrirh1.salaryrangeestimatorbackend.validation.annotations;

import fi.petrirh1.salaryrangeestimatorbackend.validation.validators.LocationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LocationValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocation {

    String message() default "Invalid location";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

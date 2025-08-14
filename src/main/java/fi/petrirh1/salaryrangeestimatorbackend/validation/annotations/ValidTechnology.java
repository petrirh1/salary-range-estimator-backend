package fi.petrirh1.salaryrangeestimatorbackend.validation.annotations;

import fi.petrirh1.salaryrangeestimatorbackend.validation.validators.TechnologyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TechnologyValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTechnology {

    String message() default "One or more technologies are invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

package fi.petrirh1.salaryrangeestimatorbackend.validation.validators;

import fi.petrirh1.salaryrangeestimatorbackend.config.ValidFormValuesConfig;
import fi.petrirh1.salaryrangeestimatorbackend.validation.annotations.ValidTechnology;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TechnologyValidator implements ConstraintValidator<ValidTechnology, String> {

    private final ValidFormValuesConfig validFormValuesConfig;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return validFormValuesConfig.getTechnologies().contains(value);
    }
}

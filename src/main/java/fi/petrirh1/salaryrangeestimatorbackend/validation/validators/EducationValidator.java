package fi.petrirh1.salaryrangeestimatorbackend.validation.validators;

import fi.petrirh1.salaryrangeestimatorbackend.config.FormAllowedValuesConfig;
import fi.petrirh1.salaryrangeestimatorbackend.validation.annotations.ValidEducation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EducationValidator implements ConstraintValidator<ValidEducation, String> {

    private final FormAllowedValuesConfig formAllowedValuesConfig;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return formAllowedValuesConfig.getEducations().contains(value);
    }
}

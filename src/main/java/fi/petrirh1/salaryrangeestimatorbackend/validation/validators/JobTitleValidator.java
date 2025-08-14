package fi.petrirh1.salaryrangeestimatorbackend.validation.validators;


import fi.petrirh1.salaryrangeestimatorbackend.config.FormAllowedValuesConfig;
import fi.petrirh1.salaryrangeestimatorbackend.validation.annotations.ValidJobTitle;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobTitleValidator implements ConstraintValidator<ValidJobTitle, String> {

    private final FormAllowedValuesConfig formAllowedValuesConfig;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return formAllowedValuesConfig.getJobTitles().contains(value);
    }
}

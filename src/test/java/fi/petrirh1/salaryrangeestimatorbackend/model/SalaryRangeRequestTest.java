package fi.petrirh1.salaryrangeestimatorbackend.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalaryRangeRequestTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    private SalaryRangeRequest validRequest() {
        SalaryRangeRequest request = new SalaryRangeRequest();
        request.setJobTitle("Software Developer");
        request.setExperience(5.0);
        request.setEducation("Ammattikorkeakoulututkinto");
        request.setIndustry("Ohjelmistokehitys");
        request.setLocation("Etätyö");
        request.setTechnologies(List.of("Java", "React"));
        request.setCurrentSalary(4000.0);
        return request;
    }

    private SalaryRangeRequest validRequestWithRequiredFields() {
        SalaryRangeRequest request = new SalaryRangeRequest();
        request.setJobTitle("Software Developer");
        request.setExperience(5.0);
        request.setIndustry("Ohjelmistokehitys");
        request.setTechnologies(List.of("Java", "React"));
        return request;
    }

    private void assertSingleViolation(SalaryRangeRequest request, String expectedMessage) {
        Set<ConstraintViolation<SalaryRangeRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @Test
    void validRequestShouldHaveNoViolations() {
        assertTrue(validator.validate(validRequest()).isEmpty());
    }

    @Test
    void validRequestWithRequiredFieldsShouldHaveNoViolations() {
        assertTrue(validator.validate(validRequestWithRequiredFields()).isEmpty());
    }

    /* Job Title */
    @Test
    void shouldFailValidationWhenJobTitleIsInvalid() {
        SalaryRangeRequest request = validRequest();
        request.setJobTitle("");
        assertSingleViolation(request, "Invalid job title");
    }

    @Test
    void shouldFailValidationWhenJobTitleIsNull() {
        SalaryRangeRequest request = validRequest();
        request.setJobTitle(null);
        assertSingleViolation(request, "must not be null");
    }

    /* Experience */
    @Test
    void shouldFailValidationWhenExperienceIsNull() {
        SalaryRangeRequest request = validRequest();
        request.setExperience(null);
        assertSingleViolation(request, "must not be null");
    }

    @Test
    void shouldFailValidationWhenExperienceIsNegative() {
        SalaryRangeRequest request = validRequest();
        request.setExperience(-1.0);
        assertSingleViolation(request, "must be greater than or equal to 0");
    }

    @Test
    void shouldFailValidationWhenExperienceIsGreaterThanMax() {
        SalaryRangeRequest request = validRequest();
        request.setExperience(65.0);
        assertSingleViolation(request, "must be less than or equal to 60");
    }

    /* Education */
    @Test
    void shouldFailValidationWhenEducationIsInvalid() {
        SalaryRangeRequest request = validRequest();
        request.setEducation("");
        assertSingleViolation(request, "Invalid education");
    }

    /* Industry */
    @Test
    void shouldFailValidationWhenIndustryIsInvalid() {
        SalaryRangeRequest request = validRequest();
        request.setIndustry("");
        assertSingleViolation(request, "Invalid industry");
    }

    @Test
    void shouldFailValidationWhenIndustryIsNull() {
        SalaryRangeRequest request = validRequest();
        request.setIndustry(null);
        assertSingleViolation(request, "must not be null");
    }

    /* Location */
    @Test
    void shouldFailValidationWhenLocationIsInvalid() {
        SalaryRangeRequest request = validRequest();
        request.setLocation("");
        assertSingleViolation(request, "Invalid location");
    }

    /* Technology */
    @Test
    void shouldFailValidationWhenTechnologyIsInvalid() {
        SalaryRangeRequest request = validRequest();
        request.setTechnologies(List.of(""));
        assertSingleViolation(request, "One or more technologies are invalid");
    }

    @Test
    void shouldFailValidationWhenTechnologyIsEmptyList() {
        SalaryRangeRequest request = validRequest();
        request.setTechnologies(List.of());
        assertSingleViolation(request, "must not be empty");
    }

    @Test
    void shouldFailValidationWhenTechnologyListHasInvalidEntry() {
        SalaryRangeRequest request = validRequest();
        request.setTechnologies(List.of("Java", "React", "_"));
        assertSingleViolation(request, "One or more technologies are invalid");
    }

    @Test
    void shouldFailValidationWhenTechnologyIsNull() {
        SalaryRangeRequest request = validRequest();
        request.setTechnologies(null);

        Set<ConstraintViolation<SalaryRangeRequest>> violations = validator.validate(request);
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(2, violations.size());
        assertAll(
                () -> assertTrue(messages.contains("must not be null")),
                () -> assertTrue(messages.contains("must not be empty"))
        );
    }

    /* Current Salary */
    @Test
    void shouldFailValidationWhenCurrentSalaryIsNegative() {
        SalaryRangeRequest request = validRequest();
        request.setCurrentSalary(-100.0);
        assertSingleViolation(request, "must be greater than or equal to 0");
    }

    @Test
    void shouldFailValidationWhenCurrentSalaryIsGreaterThanMax() {
        SalaryRangeRequest request = validRequest();
        request.setCurrentSalary(16_500.0);
        assertSingleViolation(request, "must be less than or equal to 16000");
    }

}



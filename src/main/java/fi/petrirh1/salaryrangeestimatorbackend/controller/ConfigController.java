package fi.petrirh1.salaryrangeestimatorbackend.controller;

import fi.petrirh1.salaryrangeestimatorbackend.config.ValidFormValuesConfig;
import fi.petrirh1.salaryrangeestimatorbackend.model.ValidFormValuesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class ConfigController {

    private final ValidFormValuesConfig validFormValuesConfig;

    @Cacheable("validFormValues")
    @GetMapping("/valid-form-values")
    public ValidFormValuesResponse getValidFormValues() {
        return ValidFormValuesResponse.builder()
                .jobTitles(validFormValuesConfig.getJobTitles())
                .educations(validFormValuesConfig.getEducations())
                .industries(validFormValuesConfig.getIndustries())
                .locations(validFormValuesConfig.getLocations())
                .technologies(validFormValuesConfig.getTechnologies())
                .build();
    }
}
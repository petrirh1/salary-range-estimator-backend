package fi.petrirh1.salaryrangeestimatorbackend.controller;

import fi.petrirh1.salaryrangeestimatorbackend.config.FormAllowedValuesConfig;
import fi.petrirh1.salaryrangeestimatorbackend.model.FormAllowedValuesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class ConfigController {

    private final FormAllowedValuesConfig formAllowedValuesConfig;

    @Cacheable("formAllowedValues")
    @GetMapping("/form-allowed-values")
    public FormAllowedValuesResponse getFormAllowedValues() {
        return FormAllowedValuesResponse.builder()
                .jobTitles(formAllowedValuesConfig.getJobTitles())
                .educations(formAllowedValuesConfig.getEducations())
                .industries(formAllowedValuesConfig.getIndustries())
                .locations(formAllowedValuesConfig.getLocations())
                .technologies(formAllowedValuesConfig.getTechnologies())
                .build();
    }
}
package fi.petrirh1.salaryrangeestimatorbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "form-allowed-values")
public class FormAllowedValuesConfig {

    private List<String> jobTitles;
    private List<String> locations;
    private List<String> industries;
    private List<String> educations;
    private List<String> technologies;

}

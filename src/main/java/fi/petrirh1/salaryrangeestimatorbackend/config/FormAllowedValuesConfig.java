package fi.petrirh1.salaryrangeestimatorbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "form-allowed-values")
public class FormAllowedValuesConfig {

    private List<String> jobTitles;
    private List<String> locations;
    private List<String> industries;
    private List<String> educations;
    private List<String> technologies;

}

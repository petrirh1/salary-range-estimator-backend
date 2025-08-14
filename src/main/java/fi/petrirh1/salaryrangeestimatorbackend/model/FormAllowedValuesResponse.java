package fi.petrirh1.salaryrangeestimatorbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FormAllowedValuesResponse {

    private List<String> jobTitles;
    private List<String> locations;
    private List<String> industries;
    private List<String> educations;
    private List<String> technologies;

}

package fi.petrirh1.salaryrangeestimatorbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalaryRangeResponse {

    private String salaryRange;
    private String salaryAnalysis;

}

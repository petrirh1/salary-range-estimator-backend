package fi.petrirh1.salaryrangeestimatorbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRangeResponse {

    private String salaryRange;
    private String salaryAnalysis;

}

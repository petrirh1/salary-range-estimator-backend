package fi.petrirh1.salaryrangeestimatorbackend.model;

import fi.petrirh1.salaryrangeestimatorbackend.validation.annotations.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalaryRangeRequest {

    @NotNull
    @ValidJobTitle
    private String jobTitle;

    @Min(0)
    @Max(60)
    @NotNull
    private Double experience;

    @ValidEducation
    private String education;

    @NotNull
    @ValidIndustry
    private String industry;

    @ValidLocation
    private String location;

    @NotNull
    @NotEmpty
    private List<@ValidTechnology String> technologies;

    @Min(0)
    @Max(16_000)
    private Double currentSalary;

}

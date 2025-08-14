package fi.petrirh1.salaryrangeestimatorbackend.model;

import fi.petrirh1.salaryrangeestimatorbackend.validation.annotations.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalaryRangeRequest {

    @NotBlank
    @ValidJobTitle
    private String jobTitle;

    @NotNull
    private Double experience;

    @ValidEducation
    private String education;

    @NotBlank
    @ValidIndustry
    private String industry;

    @ValidLocation
    private String location;

    @NotNull
    @NotEmpty
    private List<@ValidTechnology String> technologies;

    private Double currentSalary;

}

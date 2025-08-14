package fi.petrirh1.salaryrangeestimatorbackend.controller;

import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeRequest;
import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeResponse;
import fi.petrirh1.salaryrangeestimatorbackend.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public SalaryRangeResponse getSalaryRange(@Valid @RequestBody SalaryRangeRequest request) {
        return salaryService.getSalaryRange(request);
    }

}

package fi.petrirh1.salaryrangeestimatorbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.petrirh1.salaryrangeestimatorbackend.model.GeminiResponse;
import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeRequest;
import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SalaryServiceTest {

    private MockWebServer mockWebServer;
    private SalaryService salaryService;
    private SalaryRangeRequest request;

    private String createBody(String salaryRange, String salaryAnalysis) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            SalaryRangeResponse salary = new SalaryRangeResponse();
            salary.setSalaryRange(salaryRange);
            salary.setSalaryAnalysis(salaryAnalysis);

            String innerJson = mapper.writeValueAsString(salary);

            GeminiResponse.Part part = new GeminiResponse.Part(
                    "```json\n" + innerJson + "\n```"
            );

            GeminiResponse.Content content = new GeminiResponse.Content(
                    List.of(part),
                    "model"
            );

            GeminiResponse.Candidate candidate = new GeminiResponse.Candidate(
                    content,
                    "STOP",
                    -0.19425356590141685
            );

            GeminiResponse.UsageMetadata usage = new GeminiResponse.UsageMetadata(
                    553,
                    472,
                    1025,
                    List.of(),
                    List.of()
            );

            GeminiResponse response = new GeminiResponse(
                    List.of(candidate),
                    usage,
                    "gemini-2.0-flash",
                    "test-id"
            );

            return mapper.writeValueAsString(response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to build mock body", e);
        }
    }

    private String validSalaryRange() {
        return "4200-5000";
    }

    private String invalidSalaryRange() {
        return "4200-";
    }

    private String emptySalaryRange() {
        return "";
    }

    private String validAnalysis() {
        return """
            ### Palkka-arvio 
            ### Palkkaan vaikuttavat tekijät 
            ### Epävarmuustekijät 
            ### Neuvotteluvinkkejä
            """;
    }

    private String invalidAnalysis() {
        return """
            ### Palkka-arvio 
            ### Palkkaan vaikuttavat tekijät 
            ### Epävarmuustekijät 
            """;
    }

    private String emptyAnalysis() {
        return "";
    }

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        ObjectMapper mapper = new ObjectMapper();

        salaryService = new SalaryService(webClient, mapper);

        request = new SalaryRangeRequest();
        request.setJobTitle("jobtitle");
        request.setExperience(5.0);
        request.setEducation("education");
        request.setIndustry("industry");
        request.setTechnologies(List.of("technology"));
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getSalaryRange() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(createBody(validSalaryRange(), validAnalysis()))
                .addHeader("Content-Type", "application/json"));

        SalaryRangeResponse response = salaryService.getSalaryRange(request);
        assertThat(response.getSalaryRange()).isEqualTo("4200-5000");
    }

    @Test
    void getSalaryRangeWhenEmptyBody() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salaryService.getSalaryRange(request)
        );

        assertEquals("No valid salary range data returned from Gemini API", exception.getMessage());
    }

    @Test
    void getSalaryRangeWhenEmptyAnalysis() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(createBody(validSalaryRange(), emptyAnalysis()))
                .addHeader("Content-Type", "application/json"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salaryService.getSalaryRange(request)
        );

        assertEquals("Empty analysis", exception.getMessage());
    }

    @Test
    void getSalaryRangeWhenInvalidAnalysis() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(createBody(validSalaryRange(), invalidAnalysis()))
                .addHeader("Content-Type", "application/json"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salaryService.getSalaryRange(request)
        );

        assertEquals("Invalid analysis structure", exception.getMessage());
    }

    @Test
    void getSalaryRangeWhenInvalidSalaryRange() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(createBody(invalidSalaryRange(), validAnalysis()))
                .addHeader("Content-Type", "application/json"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salaryService.getSalaryRange(request)
        );

        assertEquals("Invalid salary range", exception.getMessage());
    }

    @Test
    void getSalaryRangeWhenEmptySalaryRange() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(createBody(emptySalaryRange(), validAnalysis()))
                .addHeader("Content-Type", "application/json"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salaryService.getSalaryRange(request)
        );

        assertEquals("Empty salary range", exception.getMessage());
    }
}

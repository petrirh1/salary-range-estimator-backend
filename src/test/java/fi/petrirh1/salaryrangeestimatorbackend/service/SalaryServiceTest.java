package fi.petrirh1.salaryrangeestimatorbackend.service;

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

    private String body = "{"
            + "\"candidates\":[{"
            + "\"content\":{\"parts\":[{\"text\":\"```json\\n{\\n  \\\"salaryRange\\\": \\\"4200-5800\\\",\\n  \\\"salaryAnalysis\\\": \\\"**Palkka-arvio:**\\\\n\\\\nFullstack Software Developerin palkka...\\\"\\n}\\n```\"}],"
            + "\"role\":\"model\"},"
            + "\"finishReason\":\"STOP\","
            + "\"avgLogprobs\":-0.19425356590141685"
            + "}],"
            + "\"usageMetadata\":{"
            + "\"promptTokenCount\":553,"
            + "\"candidatesTokenCount\":472,"
            + "\"totalTokenCount\":1025,"
            + "\"promptTokensDetails\":[{\"modality\":\"TEXT\",\"tokenCount\":553}],"
            + "\"candidatesTokensDetails\":[{\"modality\":\"TEXT\",\"tokenCount\":472}]},"
            + "\"modelVersion\":\"gemini-2.0-flash\","
            + "\"responseId\":\"lnahaNTxKY6xmNAPjJTUqAw\""
            + "}";

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        salaryService = new SalaryService(webClient);

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
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        SalaryRangeResponse response = salaryService.getSalaryRange(request);
        assertThat(response.getSalaryRange()).isEqualTo("4200-5800");
        assertThat(response.getSalaryAnalysis()).contains("Fullstack Software Developerin palkka...");
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
}

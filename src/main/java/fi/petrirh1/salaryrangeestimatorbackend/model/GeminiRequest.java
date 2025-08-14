package fi.petrirh1.salaryrangeestimatorbackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {

    @JsonProperty("contents")
    private List<Content> contents;

    @JsonProperty("system_instruction")
    private SystemInstruction systemInstruction;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        @JsonProperty("text")
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemInstruction {
        @JsonProperty("role")
        private String role;

        @JsonProperty("parts")
        private List<Part> parts;
    }
}

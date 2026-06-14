package fi.petrirh1.salaryrangeestimatorbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeRequest;
import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeResponse;
import fi.petrirh1.salaryrangeestimatorbackend.model.GeminiRequest;
import fi.petrirh1.salaryrangeestimatorbackend.model.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryService {

    private final WebClient geminiWebClient;
    private final ObjectMapper mapper;

    private final String SYSTEM_INSTRUCTIONS =
                    "Olet asiantuntija, joka arvioi palkkatasoa työmarkkinoilla Suomessa. " +
                    "Perusta arvio yleiseen markkinaymmärrykseen Suomen palkkatasosta, eri ammattien tyypillisistä palkkahaarukoista ja yleisistä toimialatrendeistä. " +
                    "Älä väitä käyttäväsi reaaliaikaista tai haettua dataa työpaikkailmoituksista tai palkkatilastoista. " +
                    "Älä keksi tarkkoja lähdeviittauksia tai viittaa yksittäisiin tietokantoihin. " +

                    "Palauta vastaus AINA JSON-muodossa. JSON-objektin tulee sisältää kolme kenttää: " +
                    "\"salaryRange\" (string), \"salaryAnalysis\" (string) ja \"confidence\" (string). " +

                    "Älä lisää mitään muuta tekstiä vastaukseesi, ainoastaan JSON-objekti. " +

                    "\"salaryRange\" sisältää palkkahaarukan muodossa \"X-Y €/kk\". " +

                    "\"salaryAnalysis\" sisältää yksityiskohtaisen analyysin Markdown-muodossa.\\n\\n" +
                    "Analyysissä:\\n" +
                    "- Käytä selkeitä otsikoita, lyhyitä kappaleita ja listauksia.\\n" +
                    "- Korosta palkkaan vaikuttavia tekijöitä (esim. kokemus, teknologiat, sijainti).\\n" +
                    "- Erota selkeästi arviot ja epävarmuustekijät.\\n" +
                    "- Lisää lopuksi käytännön vinkkejä palkkaneuvotteluihin.\\n" +
                    "- Kirjoita ammattimaisella ja selkeällä suomen kielellä.\\n\\n" +

                    "\"confidence\" sisältää arvion varmuudesta: low, medium tai high.\\n\\n" +

                    "Jos syötetiedot ovat puutteellisia tai epätarkkoja, käytä laajempaa palkkahaarukkaa ja laske confidence-tasoa.\\n\\n" +

                    "Esimerkki JSON-vastauksesta:\\n" +
                    "{\\n" +
                    "  \"salaryRange\": \"4000-5500 €/kk\",\\n" +
                    "  \"salaryAnalysis\": \"### Palkka-arvio\\\\n\\\\nSovelluskehittäjän palkkahaarukka Suomessa...\\\\n\\\\n### Palkkaan vaikuttavat tekijät\\\\n- **Kokemus:**...\\\\n\\\\n### Epävarmuustekijät\\\\n- Markkinavaihtelu...\\\\n\\\\n### Neuvotteluvinkkejä\\\\n...\",\\n" +
                    "  \"confidence\": \"medium\"\\n" +
                    "}";


    @Cacheable("salaryRangeResponse")
    public SalaryRangeResponse getSalaryRange(SalaryRangeRequest request) {
        GeminiRequest geminiRequest = generateGeminiRequest(request);
        GeminiResponse response = geminiWebClient.post()
                .bodyValue(geminiRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        if (response == null || parseGeminiText(response) == null || parseGeminiText(response).isBlank()) {
            throw new IllegalStateException("No valid salary range data returned from Gemini API");
        }

        try {
            SalaryRangeResponse salaryRangeResponse = mapper.readValue(parseGeminiText(response), SalaryRangeResponse.class);
            validateSalaryRange(salaryRangeResponse.getSalaryRange());
            validateAnalysis(salaryRangeResponse.getSalaryAnalysis());

            return salaryRangeResponse;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON response from Gemini API. Response: '{}'", response, e);
            throw new IllegalStateException("No valid salary range data returned from Gemini API", e);
        }
    }

    private void validateSalaryRange(String salaryRange) {
        if (salaryRange == null || salaryRange.isBlank()) {
            throw new IllegalStateException("Empty salary range");
        }

        boolean validSalaryRange = salaryRange.matches("\\d+-\\d+");
        if (!validSalaryRange) {
            throw new IllegalStateException("Invalid salary range");
        }
    }

    private void validateAnalysis(String analysis) {
        if (analysis == null || analysis.isBlank()) {
            throw new IllegalStateException("Empty analysis");
        }

        if (!analysis.contains("### Palkka-arvio") ||
                !analysis.contains("### Palkkaan vaikuttavat tekijät") ||
                !analysis.contains("### Epävarmuustekijät") ||
                !analysis.contains("### Neuvotteluvinkkejä")) {
            throw new IllegalStateException("Invalid analysis structure");
        }
    }

    private String parseGeminiText(GeminiResponse response) {
        return response
                .getCandidates()
                .getFirst()
                .getContent()
                .getParts()
                .getFirst()
                .getText()
                .replaceAll("^```json\\s*", "")
                .replaceAll("```$", "");
    }

    private GeminiRequest generateGeminiRequest(SalaryRangeRequest request) {
        GeminiRequest.Part contentPart = new GeminiRequest.Part(
                generateContentText(request)
        );

        GeminiRequest.Content content = new GeminiRequest.Content(List.of(contentPart));
        GeminiRequest.Part systemPart = new GeminiRequest.Part(SYSTEM_INSTRUCTIONS);
        GeminiRequest.SystemInstruction systemInstruction =
                new GeminiRequest.SystemInstruction("system", List.of(systemPart));
        GeminiRequest.GenerationConfig generationConfig =
                new GeminiRequest.GenerationConfig("application/json", 0.1);

        return new GeminiRequest(
                List.of(content),
                systemInstruction,
                generationConfig
        );
    }

    private String generateContentText(SalaryRangeRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Työnimike: ").append(request.getJobTitle()).append(". ");
        sb.append("Kokemus: ").append(request.getExperience()).append(" vuotta. ");
        if (request.getEducation() != null) {
            sb.append("Koulutustaso: ").append(request.getEducation()).append(". ");
        }
        sb.append("Toimiala: ").append(request.getIndustry()).append(". ");
        if (request.getLocation() != null) {
            sb.append("Sijainti: ").append(request.getLocation()).append(". ");
        }
        sb.append("Teknologiat: ")
                .append(String.join(", ", request.getTechnologies()))
                .append(". ");
        if (request.getCurrentSalary() != null) {
            sb.append("Nykyinen palkka: ").append(request.getCurrentSalary()).append("€/kk.");
        }

        return sb.toString();
    }
}

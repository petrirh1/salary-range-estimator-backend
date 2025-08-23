package fi.petrirh1.salaryrangeestimatorbackend.service;

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
                 "Käytä ajantasaisia tietoja työpaikkailmoituksista, palkkatilastoista ja toimialatiedoista. " +
                 "Palauta vastaus AINA JSON-muodossa. JSON-objektin tulee sisältää kaksi kenttää: " +
                 "\"salaryRange\" (string) ja \"salaryAnalysis\" (string). " +
                 "Älä lisää mitään muuta tekstiä vastaukseesi, ainoastaan JSON-objekti." +
                 "\"salaryRange\" sisältää pelkän palkkahaarukan (esim. \"4000-5500\"). " +
                 "\"salaryAnalysis\" sisältää yksityiskohtaisen analyysin Markdown-muodossa.\\n\\n" +
                 "Analyysissä:\\n" +
                 "- Käytä selkeitä otsikoita, lyhyitä kappaleita ja listauksia.\\n" +
                 "- Korosta palkkaan vaikuttavia tekijöitä (esim. kokemus, teknologiat, sijainti) erillisillä väliotsikoilla.\\n" +
                 "- Lisää lopuksi käytännön vinkkejä palkkaneuvotteluihin.\\n" +
                 "- Kirjoita ammattimaisella ja selkeällä suomen kielellä.\\n\\n" +
                 "Esimerkki JSON-vastauksesta:\\n" +
                 "{\\n" +
                 "  \"salaryRange\": \"4000-5500\",\\n" +
                 "  \"salaryAnalysis\": \"### Palkka-arvio\\\\n\\\\nSovelluskehittäjän palkkahaarukka...\\\\n\\\\n### Palkkaan vaikuttavat tekijät\\\\n- **Kokemus:**...\\\\n\\\\n### Neuvotteluvinkkejä\\\\n...\"\\n" +
                 "}";


    @Cacheable("salaryRangeResponse")
    public SalaryRangeResponse getSalaryRange(SalaryRangeRequest request) {
        GeminiRequest geminiRequest = generateGeminiRequest(request);
        GeminiResponse response = geminiWebClient.post()
                .bodyValue(geminiRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        try {
            return mapper.readValue(parseGeminiText(response), SalaryRangeResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse JSON response from Gemini API. Response: '{}'", response, e);
            throw new IllegalStateException("No valid salary range data returned from Gemini API");
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

        return new GeminiRequest(
                List.of(content),
                systemInstruction
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

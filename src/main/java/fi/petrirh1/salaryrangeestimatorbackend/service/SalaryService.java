package fi.petrirh1.salaryrangeestimatorbackend.service;

import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeRequest;
import fi.petrirh1.salaryrangeestimatorbackend.model.SalaryRangeResponse;
import fi.petrirh1.salaryrangeestimatorbackend.model.GeminiRequest;
import fi.petrirh1.salaryrangeestimatorbackend.model.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final WebClient geminiWebClient;

//    private final String SYSTEM_INSTRUCTIONS_RESTRICTED = "Olet asiantuntija, joka arvioi palkkatasoa työmarkkinoilla Suomessa. " +
//            "Käytä ajantasaisia tietoja työpaikkailmoituksista, palkkatilastoista ja toimialatiedoista, " +
//            "kun arvioit palkkahaarukkaa. Palauta pelkkä palkkahaarukka.";

    private final String SYSTEM_INSTRUCTIONS =
            "Olet asiantuntija, joka arvioi palkkatasoa työmarkkinoilla Suomessa. " +
                    "Käytä ajantasaisia tietoja työpaikkailmoituksista, palkkatilastoista ja toimialatiedoista, kun arvioit palkkahaarukkaa. " +
                    "Palauta vastaus seuraavassa muodossa: ensin pelkkä palkkahaarukka (ilman €/kk-merkintää), sitten puolipiste ';', ja sen jälkeen tiivistelmä Markdown-muodossa.\n\n" +
                    "Tiivistelmässä:\n" +
                    "- Käytä selkeitä otsikoita, lyhyitä kappaleita ja listauksia.\n" +
                    "- Tiivistä olennaisin muutamaan napakkaan virkkeeseen.\n" +
                    "- Korosta palkkaan vaikuttavia tekijöitä (esim. kokemus, teknologiat, toimiala, sijainti) erillisillä väliotsikoilla.\n" +
                    "- Lisää lopuksi käytännön vinkkejä palkkaneuvotteluihin.\n" +
                    "- Varmista, että puolipiste erottaa palkkahaarukan ja tiivistelmän selkeästi ohjelmallista käsittelyä varten.\n" +
                    "- Kirjoita ammattimaisella, selkeällä ja sujuvalla kielellä ilman toistoa.\n\n" +
                    "Esimerkki vastausmuodosta:\n" +
                    "4000-5500;\n" +
                    "**Palkka-arvio:**\n\n" +
                    "Sovelluskehittäjän palkkahaarukka 4,5 vuoden kokemuksella on 4000–5500 euroa kuukaudessa.\n\n" +
                    "**Palkkaan vaikuttavat tekijät:**\n" +
                    "- **Kokemus:** Nostaa palkkaa lähes viiden vuoden kokemuksella.\n" +
                    "- **Koulutustaso:** Koulutustaso saattaa vaikuttaa palkkaan merkittävästi.\n\n" +
                    "- **Teknologiaosaaminen:** React, Java, Spring Boot ja Angular ovat arvostettuja taitoja.\n" +
                    "- **Toimiala:** IT-konsultointi maksaa yleisesti hyvin.\n" +
                    "- **Sijainti:** Etätyö ei merkittävästi laske palkkaa vahvalla osaamisella.\n\n" +
                    "**Neuvotteluvinkkejä:**\n\n" +
                    "Perustele palkkatoiveesi osaamisella ja kokemuksella. Korosta omaa arvoasi yritykselle palkkaneuvotteluissa.";

    @Cacheable("salaryRangeResponse")
    public SalaryRangeResponse getSalaryRange(SalaryRangeRequest request) {
        GeminiRequest geminiRequest = generateGeminiRequest(request);
        GeminiResponse response = geminiWebClient.post()
                .bodyValue(geminiRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();


        String text = extractFirstCandidateText(response)
                .orElseThrow(() -> new IllegalStateException("No valid salary range data returned from Gemini API"));

        return parseSalaryRangeResponse(text);
    }

    private Optional<String> extractFirstCandidateText(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            return Optional.empty();
        }

        GeminiResponse.Candidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null || candidate.getContent().getParts() == null || candidate.getContent().getParts().isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(candidate.getContent().getParts().get(0).getText());
    }

    private SalaryRangeResponse parseSalaryRangeResponse(String text) {
        String[] splitText = text.split(";", 2); // limit to avoid unexpected splits
        if (splitText.length < 2) {
            throw new IllegalArgumentException("Invalid salary range format: " + text);
        }

        return new SalaryRangeResponse(splitText[0].trim(), splitText[1].trim());
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

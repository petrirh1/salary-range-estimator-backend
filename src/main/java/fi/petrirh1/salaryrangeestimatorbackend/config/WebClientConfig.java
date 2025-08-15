package fi.petrirh1.salaryrangeestimatorbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${gemini.base-url}")
    private String geminiBaseUrl;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Bean
    public WebClient geminiWebClient(WebClient.Builder builder) {
        return builder.baseUrl(geminiBaseUrl + "?key=" + geminiApiKey)
                .clientConnector(defaultConnector())
                .build();
    }

    private ReactorClientHttpConnector defaultConnector() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20));

        return new ReactorClientHttpConnector(httpClient);
    }

}

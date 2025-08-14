package fi.petrirh1.salaryrangeestimatorbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*"); // TODO: REMOVE, WHEN DONE DEVELOPING!
//                .allowedOrigins("http://localhost:5173")
//                .allowedMethods("POST");
    }

}

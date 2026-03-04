package com.vitalink.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200", "http://localhost:4201", "http://localhost:3000",
                        "https://blue-flower-0c40ce010.2.azurestaticapps.net",
                        "https://vitalinkdev.jdzurita12.workers.dev",
                        "https://vitalink-api-proxy.jdzurita12.workers.dev")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}

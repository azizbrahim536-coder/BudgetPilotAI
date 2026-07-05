package com.aziz.budgetpilotai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig
        implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(
            CorsRegistry registry
    ) {
        registry
                .addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://127.0.0.1:4200"
                )
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
                .allowedHeaders("*");
    }
}
package com.kahlab.easytask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500") // Permitir a origem do seu front-end
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permitir esses métodos
                .allowedHeaders("*") // Permitir todos os cabeçalhos
                .allowCredentials(true); // Permitir o envio de cookies e credenciais
    }
}


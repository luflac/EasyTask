package com.kahlab.easytask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExternalEmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("personalizations", new Object[]{
                    Map.of("to", new Object[]{Map.of("email", to)})
            });
            body.put("from", Map.of("email", "easytask.suporte@gmail.com"));
            body.put("subject", subject);
            body.put("content", new Object[]{
                    Map.of(
                            "type", "text/html",
                            "value", htmlContent
                    )
            });

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[SENDGRID ERROR] Código HTTP: " + response.getStatusCodeValue());
                System.out.println("[SENDGRID ERROR] Corpo da resposta: " + response.getBody());
                throw new RuntimeException("Falha ao enviar e-mail via SendGrid.");
            }

            System.out.println("[SENDGRID] E-mail enviado com sucesso para: " + to);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail via SendGrid: " + e.getMessage());
        }
    }
}

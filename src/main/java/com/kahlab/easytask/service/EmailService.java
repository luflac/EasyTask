package com.kahlab.easytask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ExternalEmailService externalEmailService;

    public void sendEmail(String to, String subject, String htmlContent) {
        externalEmailService.sendEmail(to, subject, htmlContent);
    }
}

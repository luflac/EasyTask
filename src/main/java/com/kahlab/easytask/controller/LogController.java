package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.LogEntryResponseDTO;
import com.kahlab.easytask.model.LogEntry;
import com.kahlab.easytask.repository.LogEntryRepository;
import com.kahlab.easytask.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogEntryRepository logEntryRepository;
    @Autowired
    private LogService logService;

    @GetMapping("/collaborator/{id}")
    @PreAuthorize("hasRole('SUPERIOR')")
    public ResponseEntity<List<LogEntryResponseDTO>> getLogsByCollaborator(@PathVariable Long id) {
        List<LogEntry> logs = logEntryRepository.findByCollaborator_IdCollaborator(id);
        List<LogEntryResponseDTO> dtos = logs.stream()
                .map(logService::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERIOR')")
    public ResponseEntity<List<LogEntryResponseDTO>> getAllLogs() {
        List<LogEntry> logs = logEntryRepository.findAll();
        List<LogEntryResponseDTO> dtos = logs.stream()
                .map(logService::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('SUPERIOR')")
    public ResponseEntity<byte[]> exportLogsToPdf() throws IOException {
        List<LogEntry> logs = logEntryRepository.findAll(Sort.by(Sort.Direction.ASC, "timestamp"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font logFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        document.add(new Paragraph("Relatório de Logs - EasyTask", titleFont));
        document.add(new Paragraph(" ")); // Espaço

        for (LogEntry log : logs) {
            String line = String.format("[%s] %s - %s: %s (por %s)",
                    log.getTimestamp(),
                    log.getEntityType(),
                    log.getAction(),
                    log.getDescription(),
                    log.getCollaborator().getName()
            );
            document.add(new Paragraph(line, logFont));
        }

        document.close();

        byte[] pdfBytes = out.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "logs-easytask.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}


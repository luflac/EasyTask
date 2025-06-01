package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.service.PdfReportService;
import com.kahlab.easytask.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final PdfReportService pdfReportService;

    public ReportController(ReportService reportService, PdfReportService pdfReportService) {
        this.reportService = reportService;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping("/priority-tasks")
    public ResponseEntity<List<TaskPriorityReportDTO>> getPriorityReport() {
        List<TaskPriorityReportDTO> report = reportService.getTasksOrderedByPriority();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/priority-tasks/pdf")
    public ResponseEntity<byte[]> downloadPriorityPdf() {
        byte[] pdf = pdfReportService.generatePriorityReportPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-prioridade.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
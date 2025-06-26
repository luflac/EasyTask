package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.*;
import com.kahlab.easytask.service.PdfReportService;
import com.kahlab.easytask.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/general/pdf")
    public ResponseEntity<byte[]> downloadGeneralReportPdf(
            @RequestParam String type,
            @RequestParam Long id
    ) {
        byte[] pdf = pdfReportService.generateGeneralReportPdf(type, id);

        String fileName = "relatorio-geral-" + type + "-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @GetMapping("/general/client/{idClient}")
    public ResponseEntity<List<TaskGeneralReportDTO>> getReportByClient(@PathVariable Long idClient) {
        List<TaskGeneralReportDTO> report = reportService.getReportByClient(idClient);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/general/collaborator/{idCollaborator}")
    public ResponseEntity<List<TaskGeneralReportDTO>> getReportByCollaborator(@PathVariable Long idCollaborator) {
        List<TaskGeneralReportDTO> report = reportService.getReportByCollaborator(idCollaborator);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsOverviewDTO> getGeneralStatistics() {
        StatisticsOverviewDTO statistics = reportService.getGeneralStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/collaborators/{id}/performance-report")
    public ResponseEntity<CollaboratorPerformanceReportDTO> getPerformanceReport(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        CollaboratorPerformanceReportDTO report = reportService.generatePerformanceReport(id, start, end);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/clients/{id}/performance-report")
    public ResponseEntity<ClientPerformanceReportDTO> getClientPerformanceReport(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        ClientPerformanceReportDTO report = reportService.generatePerformanceReportForClient(id, start, end);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/tasks/tracking")
    public ResponseEntity<List<TaskTrackingReportDTO>> getAllTasksForTracking() {
        List<TaskTrackingReportDTO> report = reportService.getAllTasksForTracking();
        return ResponseEntity.ok(report);
    }


}

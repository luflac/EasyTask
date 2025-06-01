package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.TaskGeneralReportDTO;
import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.service.PdfReportService;
import com.kahlab.easytask.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
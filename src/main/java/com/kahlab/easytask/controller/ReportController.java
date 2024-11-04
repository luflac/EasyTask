package com.kahlab.easytask.controller;

import com.kahlab.easytask.model.Report;
import com.kahlab.easytask.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        Report savedReport = reportService.saveOrUpdateReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReport);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Optional<Report> report = reportService.findReportById(id);
        return report.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/searchByTitle")
    public ResponseEntity<List<Report>> findReportsByTitle(@RequestParam String title) {
        List<Report> reports = reportService.findReportsByTitle(title);
        return reports.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reports);
    }

    @GetMapping("/searchByDateRange")
    public ResponseEntity<List<Report>> findReportsByDateRange(@RequestParam Date startDate, @RequestParam Date endDate) {
        List<Report> reports = reportService.findReportsByDateRange(startDate, endDate);
        return reports.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reports);
    }

    @GetMapping("/searchByDate")
    public ResponseEntity<List<Report>> findReportsByDate(@RequestParam Date date) {
        List<Report> reports = reportService.findReportsByDate(date);
        return reports.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(reports);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.findAllReports();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

}

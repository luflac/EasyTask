package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Report;
import com.kahlab.easytask.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // Salva ou atualiza um relatório
    public Report saveOrUpdateReport(Report report) {
        return reportRepository.save(report);
    }

    // Busca um relatório pelo ID
    public Optional<Report> findReportById(Long id) {
        return reportRepository.findById(id);
    }

    // Lista todos os relatórios
    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }

    // Deleta um relatório pelo ID
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    // Busca relatórios pelo título
    public List<Report> findReportsByTitle(String title) {
        return reportRepository.findByTitleContaining(title);
    }

    // Busca relatórios entre duas datas
    public List<Report> findReportsByDateRange(Date startDate, Date endDate) {
        return reportRepository.findByDateBetween(startDate, endDate);
    }

    //Busca relatorios em uma data especifica
    public List<Report> findReportsByDate(Date date) {
        return reportRepository.findByDate(date);
    }

}

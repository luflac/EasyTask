package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Buscar relatórios pelo título
    List<Report> findByTitleContaining(String title);

    // Buscar relatórios de uma data específica
    List<Report> findByDate(Date date);

    // Buscar relatórios em um intervalo de datas
    List<Report> findByDateBetween(Date startDate, Date endDate);

}

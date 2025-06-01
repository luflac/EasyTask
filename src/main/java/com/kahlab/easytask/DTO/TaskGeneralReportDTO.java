package com.kahlab.easytask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

    @Data
    @AllArgsConstructor
    public class TaskGeneralReportDTO {
        private String title;
        private int priority;
        private LocalDate dueDate;
        private String phaseName;
        private String otherPartyName; // Nome do cliente ou do colaborador, dependendo do contexto
    }


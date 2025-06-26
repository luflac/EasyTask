package com.kahlab.easytask.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

    @Data
    @Builder
    public class TaskResponseDTO {
        private Long idTask;
        private String title;
        private String description;
        private Short priority;
        private LocalDate dueDate;
        private LocalDate creationDate;

        private String boardName;
        private String phaseName;
        private Long phaseId;
        private String clientName;
        private String collaboratorName;
    }
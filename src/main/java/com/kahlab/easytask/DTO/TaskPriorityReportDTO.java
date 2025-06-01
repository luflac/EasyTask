package com.kahlab.easytask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TaskPriorityReportDTO {
    private String title;
    private LocalDate dueDate;
    private int priority;
    private String phaseName;
    private String clientName;
    private String collaboratorName;
}
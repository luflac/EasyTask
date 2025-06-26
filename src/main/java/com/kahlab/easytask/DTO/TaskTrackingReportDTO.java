package com.kahlab.easytask.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTrackingReportDTO {
    private Long id;
    private String title;
    private String description;
    private int priority;
    private String phase;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private String clientName;
    private String collaboratorName;
}


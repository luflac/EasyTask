package com.kahlab.easytask.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorPerformanceReportDTO {
    private Long id;
    private String name;
    private String email;
    private String position;

    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int overdueTasks;
    private double completionRate;

    private LocalDate startDate;
    private LocalDate endDate;

    private List<CollaboratorTaskDTO> tasks;
}


package com.kahlab.easytask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsOverviewDTO {
    private long totalTasks;
    private long tasksInProgress;
    private long tasksCompleted;
    private long activeClients;
    private long activeCollaborators;
    private long overdueTasks;
}

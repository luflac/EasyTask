package com.kahlab.easytask.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDTO {
    private String title;
    private String description;
    private Short priority;
    private LocalDate dueDate;

    private Long boardId;
    private Long phaseId;
    private Long clientId;
    private Long collaboratorId;
}

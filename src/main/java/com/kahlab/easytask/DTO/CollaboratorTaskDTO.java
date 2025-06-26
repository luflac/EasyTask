package com.kahlab.easytask.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorTaskDTO {
    private Long id;
    private String title;
    private int priority;
    private LocalDate dueDate;
    private String phase;
    private String clientName;        // usado no relatório do colaborador
    private String collaboratorName;  // usado no relatório do cliente

}



package com.kahlab.easytask.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogEntryResponseDTO {
    private Long id;
    private String entityType;
    private String action;
    private String description;
    private LocalDateTime timestamp;
    private CollaboratorSummaryDTO collaborator;
}

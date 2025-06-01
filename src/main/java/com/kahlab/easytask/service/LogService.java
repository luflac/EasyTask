package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.CollaboratorSummaryDTO;
import com.kahlab.easytask.DTO.LogEntryResponseDTO;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.LogEntry;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    @Autowired
    private LogEntryRepository logEntryRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;

    public void logAction(Long collaboratorId, String entityType, String action, String description) {
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        LogEntry log = new LogEntry();
        log.setCollaborator(collaborator);
        log.setEntityType(entityType);
        log.setAction(action);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());

        logEntryRepository.save(log);
    }

    public LogEntryResponseDTO toDTO(LogEntry log) {
        Collaborator c = log.getCollaborator();
        CollaboratorSummaryDTO collaboratorDTO = new CollaboratorSummaryDTO(
                c.getIdCollaborator(),
                c.getName(),
                c.getEmail()
        );

        LogEntryResponseDTO dto = new LogEntryResponseDTO();
        dto.setId(log.getId());
        dto.setEntityType(log.getEntityType());
        dto.setAction(log.getAction());
        dto.setDescription(log.getDescription());
        dto.setTimestamp(log.getTimestamp());
        dto.setCollaborator(collaboratorDTO);

        return dto;
    }

}

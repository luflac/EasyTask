package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.*;
import com.kahlab.easytask.model.Client;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.repository.ClientRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final ClientRepository clientRepository;
    @Autowired
    private final CollaboratorRepository collaboratorRepository;


    public ReportService(TaskRepository taskRepository,
                         ClientRepository clientRepository,
                         CollaboratorRepository collaboratorRepository) {
        this.taskRepository = taskRepository;
        this.clientRepository = clientRepository;
        this.collaboratorRepository = collaboratorRepository;
    }

    public List<TaskPriorityReportDTO> getTasksOrderedByPriority() {
        return taskRepository.findAllTasksOrderedByPriority();
    }

    public List<TaskGeneralReportDTO> getReportByClient(Long idClient) {
        return taskRepository.findTasksByClientId(idClient);
    }

    public List<TaskGeneralReportDTO> getReportByCollaborator(Long idCollaborator) {
        return taskRepository.findTasksByCollaboratorId(idCollaborator);
    }

    public StatisticsOverviewDTO getGeneralStatistics() {

        long totalTasks = taskRepository.count();
        long tasksCompleted = taskRepository.countByPhaseName("CONCLUÍDO");
        long tasksInProgress = taskRepository.countByPhaseNameNot("CONCLUÍDO");
        long activeClients = clientRepository.count();
        long activeCollaborators = collaboratorRepository.count();
        long overdueTasks = taskRepository.countOverdueTasks(LocalDate.now());

        return new StatisticsOverviewDTO(
                totalTasks,
                tasksInProgress,
                tasksCompleted,
                activeClients,
                activeCollaborators,
                overdueTasks
        );
    }

    public CollaboratorPerformanceReportDTO generatePerformanceReport(Long collaboratorId, LocalDate start, LocalDate end) {
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado"));

        List<Task> tasks;

        // Se as datas forem fornecidas, filtra por creationDate entre elas
        if (start != null && end != null) {
            tasks = taskRepository.findByCollaboratorIdAndDateRange(collaboratorId, start, end);
        } else {
            // Caso contrário, retorna todas as tarefas do colaborador
            tasks = taskRepository.findByCollaboratorIdCollaborator(collaboratorId);
        }

        int total = tasks.size();
        int completed = (int) tasks.stream()
                .filter(t -> t.getPhase().getName().equalsIgnoreCase("CONCLUÍDO"))
                .count();

        int inProgress = total - completed;

        int overdue = (int) tasks.stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().isBefore(LocalDate.now()) &&
                        !t.getPhase().getName().equalsIgnoreCase("CONCLUÍDO"))
                .count();

        double completionRate = total > 0 ? (completed * 100.0) / total : 0;

        List<CollaboratorTaskDTO> taskDTOs = tasks.stream().map(t -> {
            CollaboratorTaskDTO dto = new CollaboratorTaskDTO();
            dto.setId(t.getIdTask());
            dto.setTitle(t.getTitle());
            dto.setPriority(t.getPriority());
            dto.setDueDate(t.getDueDate());
            dto.setPhase(t.getPhase().getName());
            dto.setClientName(t.getClient().getName());
            return dto;
        }).collect(Collectors.toList());

        return CollaboratorPerformanceReportDTO.builder()
                .id(collaborator.getIdCollaborator())
                .name(collaborator.getName())
                .email(collaborator.getEmail())
                .position(collaborator.getPosition())
                .totalTasks(total)
                .completedTasks(completed)
                .inProgressTasks(inProgress)
                .overdueTasks(overdue)
                .completionRate(completionRate)
                .startDate(start)
                .endDate(end)
                .tasks(taskDTOs)
                .build();
    }

    public ClientPerformanceReportDTO generatePerformanceReportForClient(Long clientId, LocalDate start, LocalDate end) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        List<Task> tasks;

        if (start != null && end != null) {
            tasks = taskRepository.findByClientIdAndDateRange(clientId, start, end);
        } else {
            tasks = taskRepository.findByClientIdClient(clientId);
        }

        int total = tasks.size();
        int completed = (int) tasks.stream()
                .filter(t -> t.getPhase().getName().equalsIgnoreCase("CONCLUÍDO"))
                .count();

        int inProgress = total - completed;

        int overdue = (int) tasks.stream()
                .filter(t -> t.getDueDate() != null &&
                        t.getDueDate().isBefore(LocalDate.now()) &&
                        !t.getPhase().getName().equalsIgnoreCase("CONCLUÍDO"))
                .count();

        double completionRate = total > 0 ? (completed * 100.0) / total : 0;

        List<CollaboratorTaskDTO> taskDTOs = tasks.stream().map(t -> {
            CollaboratorTaskDTO dto = new CollaboratorTaskDTO();
            dto.setId(t.getIdTask());
            dto.setTitle(t.getTitle());
            dto.setPriority(t.getPriority());
            dto.setDueDate(t.getDueDate());
            dto.setPhase(t.getPhase().getName());
            dto.setCollaboratorName(t.getCollaborator().getName());

            return dto;
        }).collect(Collectors.toList());

        return ClientPerformanceReportDTO.builder()
                .id(client.getIdClient())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .totalTasks(total)
                .completedTasks(completed)
                .inProgressTasks(inProgress)
                .overdueTasks(overdue)
                .completionRate(completionRate)
                .startDate(start)
                .endDate(end)
                .tasks(taskDTOs)
                .build();
    }

    public List<TaskTrackingReportDTO> getAllTasksForTracking() {
        return taskRepository.findAllTasksForTracking();
    }

}
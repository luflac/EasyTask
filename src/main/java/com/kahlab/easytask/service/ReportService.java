package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.StatisticsOverviewDTO;
import com.kahlab.easytask.DTO.TaskGeneralReportDTO;
import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.repository.ClientRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
}



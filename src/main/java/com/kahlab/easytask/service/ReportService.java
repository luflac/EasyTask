package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.TaskGeneralReportDTO;
import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private final TaskRepository taskRepository;

    public ReportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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
}



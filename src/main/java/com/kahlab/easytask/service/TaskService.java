package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Client;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.Phase;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.repository.ClientRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.PhaseRepository;
import com.kahlab.easytask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PhaseRepository phaseRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;


    public Task createTask(Task task) {
        // Validar e associar o Phase
        Phase phase = phaseRepository.findById(task.getPhase().getIdPhase())
                .orElseThrow(() -> new RuntimeException("Phase não encontrada!"));
        task.setPhase(phase);

        // Validar e associar o Client
        Client client = clientRepository.findById(task.getClient().getIdClient())
                .orElseThrow(() -> new RuntimeException("Client não encontrado!"));
        task.setClient(client);

        // Validar e associar o Collaborator
        Collaborator collaborator = collaboratorRepository.findById(task.getCollaborator().getIdCollaborator())
                .orElseThrow(() -> new RuntimeException("Collaborator não encontrado!"));
        task.setCollaborator(collaborator);

        return taskRepository.save(task);
    }

    // Atualizar tarefa
    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(existingTask -> {
            // Atualizar campos simples
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setDueDate(updatedTask.getDueDate());

            // Validar e associar Phase
            if (updatedTask.getPhase() != null) {
                Phase phase = phaseRepository.findById(updatedTask.getPhase().getIdPhase())
                        .orElseThrow(() -> new RuntimeException("Phase not found with ID: " + updatedTask.getPhase().getIdPhase()));
                existingTask.setPhase(phase);
            }

            // Validar e associar Client
            if (updatedTask.getClient() != null) {
                Client client = clientRepository.findById(updatedTask.getClient().getIdClient())
                        .orElseThrow(() -> new RuntimeException("Client not found with ID: " + updatedTask.getClient().getIdClient()));
                existingTask.setClient(client);
            }

            // Validar e associar Collaborator
            if (updatedTask.getCollaborator() != null) {
                Collaborator collaborator = collaboratorRepository.findById(updatedTask.getCollaborator().getIdCollaborator())
                        .orElseThrow(() -> new RuntimeException("Collaborator not found with ID: " + updatedTask.getCollaborator().getIdCollaborator()));
                existingTask.setCollaborator(collaborator);
            }

            return taskRepository.save(existingTask);
        }).orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }

    // Movimentar tarefa para outro estágio
    public Task moveTaskToPhase(Long taskId, Long phaseId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with ID: " + phaseId));

        task.setPhase(phase);
        return taskRepository.save(task);
    }

    // Busca uma tarefa pelo ID
    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Lista todas as tarefas
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    // Deleta uma tarefa pelo ID
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // Busca tarefas por estagio
    public List<Task> findTasksByPhase(Long idPhase) {
        return taskRepository.findByPhaseIdPhase(idPhase);
    }

    // Busca tarefas por prioridade
    public List<Task> findTasksByPriority(int priority) {
        return taskRepository.findByPriority(priority);
    }

    // Busca tarefas associadas a um colaborador específico
    public List<Task> findTasksByCollaboratorId(Long idCollaborator) {
        return taskRepository.findByCollaboratorIdCollaborator(idCollaborator);
    }

    //Busca tarefas associadas a um cliente especifico
    public List<Task> findTasksByClientId(Long clientId) {
        return taskRepository.findByClientIdClient(clientId);
    }

    // Busca tarefas por data de criação anterior a uma data específica
    public List<Task> findTasksByCreationDateBefore(Date creationDate) {
        return taskRepository.findByCreationDateBefore(creationDate);
    }
    // Relatório de Collaborator
    public Map<String, List<Task>> getPerformanceReportByCollaborator(Long collaboratorId) {
        Map<String, List<Task>> report = new HashMap<>();

        // Buscar tarefas por estagio (phase)
        List<Task> completedTasks = taskRepository.findByCollaboratorIdCollaboratorAndPhaseIdPhase(collaboratorId, 3L);
        List<Task> inProgressTasks = taskRepository.findByCollaboratorIdCollaboratorAndPhaseIdPhase(collaboratorId, 2L);
        List<Task> pendingTasks = taskRepository.findByCollaboratorIdCollaboratorAndPhaseIdPhase(collaboratorId, 5L);

        // Agrupar no relatório
        report.put("Concluídas", completedTasks);
        report.put("Em Andamento", inProgressTasks);
        report.put("À Fazer", pendingTasks);

        return report;
    }

    // Relatório Geral
    public Map<String, Object> getGeneralStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // IDs das fases
        Long completedPhaseId = 3L;
        Long inProgressPhaseId = 2L;
        Long toDoPhaseId = 5L;

        // Estatísticas de tarefas
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByPhaseIdPhase(completedPhaseId);
        long inProgressTasks = taskRepository.countByPhaseIdPhase(inProgressPhaseId);
        long toDoTasks = taskRepository.countByPhaseIdPhase(toDoPhaseId);

        // Estatísticas de clientes
        long totalClients = clientRepository.count();

        // Consolidar no relatório
        statistics.put("Tarefas Atribuídas: ", totalTasks);
        statistics.put("Tarefas Concluídas: ", completedTasks);
        statistics.put("Tarefas Em Execução: ", inProgressTasks);
        statistics.put("Tarefas Á Fazer: ", toDoTasks);
        statistics.put("Clientes Ativos:", totalClients);

        return statistics;
    }

}

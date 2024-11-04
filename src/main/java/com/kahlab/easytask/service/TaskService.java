package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Salva ou atualiza uma tarefa
    public Task saveOrUpdateTask(Task task) {
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
    public List<Task> findTasksByPhase(Long phase) {
        return taskRepository.findByPhaseId(phase);
    }

    // Busca tarefas por prioridade
    public List<Task> findTasksByPriority(int priority) {
        return taskRepository.findByPriority(priority);
    }

    // Busca tarefas associadas a um colaborador específico
    public List<Task> findTasksByCollaboratorId(Long collaboratorId) {
        return taskRepository.findByCollaboratorId(collaboratorId);
    }

    //Busca tarefas associadas a um cliente especifico
    public List<Task> findTasksByClientId(Long clientId) {
        return taskRepository.findByClientId(clientId);
    }

    // Busca tarefas por data de criação anterior a uma data específica
    public List<Task> findTasksByCreationDateBefore(Date creationDate) {
        return taskRepository.findByCreationDateBefore(creationDate);
    }

    // Busca tarefas com data de conclusão entre duas datas
    public List<Task> findTasksByCompletionDateBetween(Date startDate, Date endDate) {
        return taskRepository.findByCompletionDateBetween(startDate, endDate);
    }

}

package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Buscar tarefas por prioridade
    List<Task> findByPriority(int priority);

    // Buscar tarefas associadas a um colaborador específico
    List<Task> findByCollaboratorId(Long collaboratorId);

    // Buscar tarefas associadas a um cliente específico
    List<Task> findByClientId(Long clientId);

    // Buscar tarefas por estagio (Phase)
    List<Task> findByPhaseId(Long phaseId);

    // Buscar tarefas com data de criação anterior a uma data específica
    List<Task> findByCreationDateBefore(Date creationDate);

    // Buscar tarefas com data de conclusão entre duas datas
    List<Task> findByCompletionDateBetween(Date startDate, Date endDate);
}



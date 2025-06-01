package com.kahlab.easytask.repository;

import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Contar total de tarefas
    long count();

    // Contar tarefas por estágio
    long countByPhaseIdPhase(Long idPhase);

    // Contar tarefas em cada estágio
    @Query("SELECT t.phase.name, COUNT(t) FROM Task t GROUP BY t.phase.name")
    List<Object[]> countTasksByPhase();

    List<Task> findByBoardId(Long boardId);

    // Buscar tarefas por prioridade
    List<Task> findByPriority(int priority);

    // Buscar tarefas associadas a um colaborador específico
    List<Task> findByCollaboratorIdCollaborator(Long idCollaborator);

    // Buscar tarefas associadas a um cliente específico
    List<Task> findByClientIdClient(Long idClient);

    // Buscar tarefas por estagio (Phase)
    List<Task> findByPhaseIdPhase(Long idPhase);

    // Buscar tarefas de determinado estagio por colaborador
    List<Task> findByCollaboratorIdCollaboratorAndPhaseIdPhase(Long idCollaborator, Long idPhase);

    // Buscar tarefas com data de criação anterior a uma data específica
    List<Task> findByCreationDateBefore(Date creationDate);

    @Query("""
    SELECT new com.kahlab.easytask.DTO.TaskPriorityReportDTO(
        t.title,
        t.dueDate,
        t.priority,
        t.phase.name,
        t.client.name,
        t.collaborator.name
    )
    FROM Task t
    ORDER BY t.priority DESC
""")
    List<TaskPriorityReportDTO> findAllTasksOrderedByPriority();

}



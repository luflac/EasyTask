package com.kahlab.easytask.repository;

import com.kahlab.easytask.DTO.TaskGeneralReportDTO;
import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.DTO.TaskTrackingReportDTO;
import com.kahlab.easytask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    //QUERY do relátorio de prioridade
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

    //QUERY do relátorio de tarefas (cliente)
    @Query("""
    SELECT new com.kahlab.easytask.DTO.TaskGeneralReportDTO(
        t.title,
        t.priority,
        t.dueDate,
        t.phase.name,
        t.collaborator.name
    )
    FROM Task t
    WHERE t.client.idClient = :idClient
    ORDER BY t.priority DESC
""")
    List<TaskGeneralReportDTO> findTasksByClientId(Long idClient);

    //QUERY do relátorio de tarefas (colaborador)
    @Query("""
    SELECT new com.kahlab.easytask.DTO.TaskGeneralReportDTO(
        t.title,
        t.priority,
        t.dueDate,
        t.phase.name,
        t.client.name
    )
    FROM Task t
    WHERE t.collaborator.idCollaborator = :idCollaborator
    ORDER BY t.priority DESC
""")
    List<TaskGeneralReportDTO> findTasksByCollaboratorId(Long idCollaborator);

    @Query("SELECT c.name FROM Client c WHERE c.idClient = :id")
    String findClientNameById(Long id);

    @Query("SELECT c.name FROM Collaborator c WHERE c.idCollaborator = :id")
    String findCollaboratorNameById(Long id);

    // Conta tarefas por nome da fase
    @Query("SELECT COUNT(t) FROM Task t WHERE t.phase.name = :phaseName")
    long countByPhaseName(@Param("phaseName") String phaseName);

    // Conta tarefas exceto uma fase específica
    @Query("SELECT COUNT(t) FROM Task t WHERE t.phase.name <> :phaseName")
    long countByPhaseNameNot(@Param("phaseName") String phaseName);

    // Conta tarefas atrasadas (prazo vencido e não concluídas)
    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :today AND t.phase.name <> 'CONCLUÍDO'")
    long countOverdueTasks(@Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.collaborator.idCollaborator = :id AND t.creationDate BETWEEN :start AND :end")
    List<Task> findByCollaboratorIdAndDateRange(@Param("id") Long id, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT t FROM Task t WHERE t.client.idClient = :id AND t.creationDate BETWEEN :start AND :end")
    List<Task> findByClientIdAndDateRange(@Param("id") Long id, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
    SELECT new com.kahlab.easytask.DTO.TaskTrackingReportDTO(
        t.idTask,
        t.title,
        t.description,
        t.priority,
        t.phase.name,
        t.creationDate,
        t.dueDate,
        t.client.name,
        t.collaborator.name
    )
    FROM Task t
    ORDER BY t.dueDate ASC
""")
    List<TaskTrackingReportDTO> findAllTasksForTracking();

}
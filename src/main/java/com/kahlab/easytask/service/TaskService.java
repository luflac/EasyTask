package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.TaskDTO;
import com.kahlab.easytask.DTO.TaskResponseDTO;
import com.kahlab.easytask.model.*;
import com.kahlab.easytask.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
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
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private PhaseBoardRepository phaseBoardRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LogService logService;

    public Task createTask(TaskDTO dto) {
        // Buscar o Board
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board não encontrado"));

        // Buscar o Phase
        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new RuntimeException("Phase não encontrada"));

        // Validar se Phase pertence ao Board
        PhaseBoardId pbId = new PhaseBoardId(phase.getIdPhase(), board.getId());
        if (!phaseBoardRepository.existsById(pbId)) {
            throw new RuntimeException("Essa fase não pertence ao quadro informado.");
        }

        // Buscar o Client
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client não encontrado"));

        // Buscar o Collaborator
        Collaborator collaborator = collaboratorRepository.findById(dto.getCollaboratorId())
                .orElseThrow(() -> new RuntimeException("Collaborator não encontrado"));

        // Criar e preencher a Task
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setCreationDate(LocalDate.now());

        task.setBoard(board);
        task.setPhase(phase);
        task.setClient(client);
        task.setCollaborator(collaborator);

        // Salvar a tarefa
        Task savedTask = taskRepository.save(task);

        logService.logAction(
                collaborator.getIdCollaborator(),
                "TASK",
                "CREATE",
                "Tarefa '" + task.getTitle() + "' criada no board '" + board.getName() +
                        "' com prioridade " + task.getPriority() +
                        " atribuída ao colaborador '" + collaborator.getName() + "'"
        );

        // Enviar e-mail para o colaborador
        String emailBody = buildNewTaskEmail(collaborator.getName(), task.getTitle());
        emailService.sendEmail(
                collaborator.getEmail(),
                "Nova tarefa atribuída a você!",
                emailBody
        );

        return savedTask;
    }

    // Resposta da tarefa
    public TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
                .idTask(task.getIdTask())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .creationDate(task.getCreationDate())
                .boardName(task.getBoard().getName())
                .phaseName(task.getPhase().getName())
                .clientName(task.getClient().getName())
                .collaboratorName(task.getCollaborator().getName())
                .build();
    }

    public List<TaskResponseDTO> toDTOList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toDTO)
                .toList();
    }

    // Atualizar tarefa
    public Task updateTask(Long id, TaskDTO dto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        // Salvar a fase atual antes de atualizar
        Phase oldPhase = existingTask.getPhase();

        // Atualizar campos simples
        existingTask.setTitle(dto.getTitle());
        existingTask.setDescription(dto.getDescription());
        existingTask.setPriority(dto.getPriority());
        existingTask.setDueDate(dto.getDueDate());

        // Buscar e validar Board
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found"));
        existingTask.setBoard(board);

        // Buscar e validar Phase
        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new RuntimeException("Phase not found"));
        existingTask.setPhase(phase);

        // Validação: a fase deve pertencer ao board
        PhaseBoardId pbId = new PhaseBoardId(phase.getIdPhase(), board.getId());
        if (!phaseBoardRepository.existsById(pbId)) {
            throw new RuntimeException("Essa fase não pertence ao quadro informado.");
        }

        // Buscar e setar Client
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        existingTask.setClient(client);

        // Buscar e setar Collaborator
        Collaborator collaborator = collaboratorRepository.findById(dto.getCollaboratorId())
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));
        existingTask.setCollaborator(collaborator);

        // Verificar se houve mudança de fase
        boolean phaseChanged = oldPhase != null && !oldPhase.getIdPhase().equals(phase.getIdPhase());

        // Salvar a tarefa antes de notificar
        Task updated = taskRepository.save(existingTask);

        // ✅ REGISTRO DE LOG
        logService.logAction(
                collaborator.getIdCollaborator(),
                "TASK",
                "UPDATE",
                "Tarefa '" + updated.getTitle() + "' foi atualizada no board '" + board.getName() + "'"
        );

        // Enviar e-mail apenas após persistência
        if (phaseChanged) {
            String emailBody = buildTaskPhaseChangeEmail(
                    updated.getCollaborator().getName(),
                    updated.getTitle(),
                    updated.getPhase().getName()
            );

            emailService.sendEmail(
                    updated.getCollaborator().getEmail(),
                    "Sua tarefa mudou de fase!",
                    emailBody
            );
        }

        return updated;
    }


    // Movimentar tarefa para outro estágio
    public Task moveTaskToPhase(Long taskId, Long phaseId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        Phase newPhase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with ID: " + phaseId));

        // Verifica se a fase mudou de fato
        if (!task.getPhase().getIdPhase().equals(phaseId)) {
            Phase oldPhase = task.getPhase();
            task.setPhase(newPhase);
            Task updatedTask = taskRepository.save(task);

            // Enviar e-mail de notificação
            Collaborator collaborator = task.getCollaborator();
            String emailBody = buildTaskPhaseChangeEmail(
                    collaborator.getName(),
                    task.getTitle(),
                    newPhase.getName()
            );

            emailService.sendEmail(
                    collaborator.getEmail(),
                    "Sua tarefa mudou de fase!",
                    emailBody
            );

            // ✅ REGISTRO DE LOG
            logService.logAction(
                    collaborator.getIdCollaborator(),
                    "TASK",
                    "MOVE",
                    "Tarefa '" + task.getTitle() + "' movida da fase '" + oldPhase.getName() + "' para '" + newPhase.getName() + "'"
            );

            return updatedTask;
        }

        return task;
    }

    // Movimentar tarefa para outro quadro
    public TaskResponseDTO moveTaskToBoard(Long taskId, Long newBoardId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        Board newBoard = boardRepository.findById(newBoardId)
                .orElseThrow(() -> new RuntimeException("Board não encontrado"));

        Phase currentPhase = task.getPhase();
        PhaseBoardId pbId = new PhaseBoardId(currentPhase.getIdPhase(), newBoard.getId());

        // Se a fase atual da tarefa NÃO pertence ao novo board...
        if (!phaseBoardRepository.existsById(pbId)) {
            // Buscar as fases do novo board
            List<Phase> newBoardPhases = newBoard.getPhases().stream()
                    .map(pb -> pb.getPhase())
                    .sorted(Comparator.comparingInt(Phase::getSequence)) // ordena pela sequência
                    .toList();

            if (newBoardPhases.isEmpty()) {
                throw new RuntimeException("O novo board não possui fases.");
            }

            // Atribuir a primeira fase do novo board
            task.setPhase(newBoardPhases.get(0));
        }

        // Atualiza o board normalmente
        task.setBoard(newBoard);
        return toDTO(taskRepository.save(task));
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));

        // ✅ REGISTRO DE LOG
        logService.logAction(
                task.getCollaborator().getIdCollaborator(),
                "TASK",
                "DELETE",
                "Tarefa '" + task.getTitle() + "' foi excluída do board '" + task.getBoard().getName() + "'"
        );

        taskRepository.delete(task);
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

    public List<TaskResponseDTO> findTasksByBoard(Long boardId) {
        List<Task> tasks = taskRepository.findByBoardId(boardId);
        return toDTOList(tasks);
    }

    public String buildNewTaskEmail(String collaboratorName, String taskTitle) {
        return """
        <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2 style="color: #4CAF50;">Nova tarefa atribuída a você!</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>Você recebeu uma nova tarefa:</p>
                <blockquote style="border-left: 4px solid #4CAF50; padding-left: 10px; color: #555;">
                    <em>%s</em>
                </blockquote>
                <p>Acesse o EasyTask para mais detalhes.</p>
                <br/>
                <p style="font-size: 12px; color: #999;">Equipe EasyTask</p>
            </body>
        </html>
    """.formatted(collaboratorName, taskTitle);
    }

    private String buildTaskPhaseChangeEmail(String collaboratorName, String taskTitle, String newPhase) {
        return """
        <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2 style="color: #2196F3;">Atualização na sua tarefa!</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>A tarefa <strong>%s</strong> foi movida para a fase:</p>
                <div style="background-color: #f0f0f0; padding: 10px; border-left: 4px solid #2196F3;">
                    %s
                </div>
                <p>Acompanhe o progresso no sistema.</p>
                <br/>
                <p style="font-size: 12px; color: #999;">Equipe EasyTask</p>
            </body>
        </html>
    """.formatted(collaboratorName, taskTitle, newPhase);
    }



}

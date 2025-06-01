package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.TaskDTO;
import com.kahlab.easytask.DTO.TaskResponseDTO;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskDTO task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(taskService.toDTO(createdTask));
    }

    @PostMapping("/{idTask}/move")
    public ResponseEntity<Task> moveTaskToPhase(@PathVariable Long idTask, @RequestBody Long idPhase) {
        try {
            Task updatedTask = taskService.moveTaskToPhase(idTask, idPhase);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{taskId}/move-board/{newBoardId}")
    public ResponseEntity<TaskResponseDTO> moveTaskToBoard(@PathVariable Long taskId, @PathVariable Long newBoardId) {
        TaskResponseDTO dto = taskService.moveTaskToBoard(taskId, newBoardId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{idTask}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long idTask, @RequestBody TaskDTO task) {
        try {
            Task updatedTask = taskService.updateTask(idTask, task);
            return ResponseEntity.ok(taskService.toDTO(updatedTask));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(taskService.toDTOList(taskService.findAllTasks()));
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(taskService.findTasksByBoard(boardId));
    }

    @GetMapping("/{idTask}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long idTask) {
        return taskService.findTaskById(idTask)
                .map(taskService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/collaborator/{idCollaborator}")
    public ResponseEntity<List<Task>> getTasksByCollaborator(@PathVariable Long idCollaborator) {
        List<Task> tasks = taskService.findTasksByCollaboratorId(idCollaborator);
        return ResponseEntity.ok(tasks);
    }

    // Relatório de tarefas por cliente
    @GetMapping("/report/client/{clientId}")
    public ResponseEntity<List<Task>> getTasksByClient(@PathVariable Long clientId) {
        List<Task> tasks = taskService.findTasksByClientId(clientId);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    // Relatório de estatísticas gerais
    @GetMapping("/report/general-statistics")
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        Map<String, Object> statistics = taskService.getGeneralStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/phase/{idPhase}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByPhase(@PathVariable Long idPhase) {
        List<Task> tasks = taskService.findTasksByPhase(idPhase);
        return ResponseEntity.ok(taskService.toDTOList(taskService.findTasksByPhase(idPhase)));
    }

    @DeleteMapping("/{idTask}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long idTask) {
        taskService.deleteTask(idTask);
        return ResponseEntity.noContent().build();
    }

}

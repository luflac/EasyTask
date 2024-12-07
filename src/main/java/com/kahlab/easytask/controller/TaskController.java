package com.kahlab.easytask.controller;

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
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
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

    @PutMapping("/{idTask}")
    public ResponseEntity<Task> updateTask(@PathVariable Long idTask, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(idTask, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{idTask}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long idTask) {
        return taskService.findTaskById(idTask)
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
    public ResponseEntity<List<Task>> getTasksByPhase(@PathVariable Long idPhase) {
        List<Task> tasks = taskService.findTasksByPhase(idPhase);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{idTask}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long idTask) {
        taskService.deleteTask(idTask);
        return ResponseEntity.noContent().build();
    }

}

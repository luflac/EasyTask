package com.kahlab.easytask.controller;


import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskService.saveOrUpdateTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.findTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Novo endpoint para buscar tarefas pela fase
    @GetMapping("/searchByPhase")
    public ResponseEntity<List<Task>> findTasksByPhase(@RequestParam Long phase) {
        List<Task> tasks = taskService.findTasksByPhase(phase);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    // Novo endpoint para buscar tarefas pela prioridade
    @GetMapping("/searchByPriority")
    public ResponseEntity<List<Task>> findTasksByPriority(@RequestParam int priority) {
        List<Task> tasks = taskService.findTasksByPriority(priority);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    // Novo endpoint para buscar tarefas associadas a um colaborador específico
    @GetMapping("/searchByCollaborator")
    public ResponseEntity<List<Task>> findTasksByCollaboratorId(@RequestParam Long collaboratorId) {
        List<Task> tasks = taskService.findTasksByCollaboratorId(collaboratorId);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    // Novo endpoint para buscar tarefas associadas a um cliente específico
    @GetMapping("/searchByClient")
    public ResponseEntity<List<Task>> findTasksByClientId(@RequestParam Long clientId) {
        List<Task> tasks = taskService.findTasksByClientId(clientId);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    // Novo endpoint para buscar tarefas com data de criação anterior a uma data específica
    @GetMapping("/searchByCreationDateBefore")
    public ResponseEntity<List<Task>> findTasksByCreationDateBefore(@RequestParam Date creationDate) {
        List<Task> tasks = taskService.findTasksByCreationDateBefore(creationDate);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    // Novo endpoint para buscar tarefas com data de conclusão entre duas datas
    @GetMapping("/searchByCompletionDateRange")
    public ResponseEntity<List<Task>> findTasksByCompletionDateBetween(@RequestParam Date startDate, @RequestParam Date endDate) {
        List<Task> tasks = taskService.findTasksByCompletionDateBetween(startDate, endDate);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAllTasks();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}

package com.kahlab.easytask.controller;


import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.service.CollaboratorService;
import com.kahlab.easytask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/collaborators")
public class CollaboratorController {

    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Collaborator> createCollaborator(@RequestBody Collaborator collaborator) {
        Collaborator savedCollaborator = collaboratorService.saveOrUpdateCollaborator(collaborator);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollaborator);
    }

    @PutMapping("/{idCollaborator}")
    public ResponseEntity<Collaborator> updateCollaborator(@PathVariable Long idCollaborator, @RequestBody Collaborator collaborator) {
        try {
            Collaborator updatedCollaborator = collaboratorService.updateCollaborator(idCollaborator, collaborator);
            return ResponseEntity.ok(updatedCollaborator);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{idCollaborator}")
    public ResponseEntity<Collaborator> getCollaboratorById(@PathVariable Long idCollaborator) {
        Optional<Collaborator> collaborator = collaboratorService.findCollaboratorById(idCollaborator);
        return collaborator.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/searchByName")
    public ResponseEntity<List<Collaborator>> findCollaboratorByName(@RequestParam String name) {
        List<Collaborator> collaborators = collaboratorService.findCollaboratorByName(name);
        return collaborators.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(collaborators);
    }

    @GetMapping("/searchByPosition")
    public ResponseEntity<List<Collaborator>> findCollaboratorByPosition(@RequestParam String position) {
        List<Collaborator> collaborators = collaboratorService.findCollaboratorByPosition(position);
        return collaborators.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(collaborators);
    }

    @GetMapping
    public List<Collaborator> getAllCollaborators() {
        return collaboratorService.findAllCollaborators();
    }

    @DeleteMapping("/{idCollaborator}")
    public ResponseEntity<Void> deleteCollaborator(@PathVariable Long idCollaborator) {
        collaboratorService.deleteCollaborator(idCollaborator);
        return ResponseEntity.noContent().build();
    }

    // Listar tarefas atribuídas a um colaborador específico
    @GetMapping("/{idCollaborator}/tasks")
    public ResponseEntity<List<Task>> getTasksByCollaborator(@PathVariable Long idCollaborator) {
        List<Task> tasks = taskService.findTasksByCollaboratorId(idCollaborator);
        return ResponseEntity.ok(tasks);
    }

    // Relatório de desempenho por colaborador
    @GetMapping("/{idCollaborator}/performance-report")
    public ResponseEntity<Map<String, List<Task>>> getPerformanceReportByCollaborator(@PathVariable Long idCollaborator) {
        Map<String, List<Task>> report = taskService.getPerformanceReportByCollaborator(idCollaborator);
        if (report.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(report);
    }

}

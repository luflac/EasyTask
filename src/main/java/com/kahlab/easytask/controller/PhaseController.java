package com.kahlab.easytask.controller;


import com.kahlab.easytask.model.Phase;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.service.PhaseService;
import com.kahlab.easytask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/phases")
public class PhaseController {

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Phase> createPhase(@RequestBody Phase phase) {
        Phase savedPhase = phaseService.saveOrUpdatePhase(phase);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPhase);
    }

    @PutMapping("/{idPhase}")
    public ResponseEntity<Phase> updatePhase(@PathVariable Long idPhase, @RequestBody Phase phase) {
        try {
            Phase updatedPhase = phaseService.updatePhase(idPhase, phase);
            return ResponseEntity.ok(updatedPhase);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{idPhase}")
    public ResponseEntity<Phase> getPhaseById(@PathVariable Long idPhase) {
        Optional<Phase> phase = phaseService.findPhaseById(idPhase);
        return phase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/searchByName")
    public ResponseEntity<Phase> findPhaseByName(@RequestParam String name) {
        Optional<Phase> phase = phaseService.findPhaseByName(name);
        return phase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Phase> getAllPhases() {
        return phaseService.findAllPhases();
    }

    @DeleteMapping("/{idPhase}")
    public ResponseEntity<Void> deletePhase(@PathVariable Long idPhase) {
        phaseService.deletePhase(idPhase);
        return ResponseEntity.noContent().build();
    }

    // Listar tarefas associadas a uma fase específica
    @GetMapping("/{idPhase}/tasks")
    public ResponseEntity<List<Task>> getTasksByPhase(@PathVariable Long idPhase) {
        List<Task> tasks = taskService.findTasksByPhase(idPhase);
        return ResponseEntity.ok(tasks);
    }

}

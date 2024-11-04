package com.kahlab.easytask.controller;


import com.kahlab.easytask.model.Phase;
import com.kahlab.easytask.service.PhaseService;
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

    @PostMapping
    public ResponseEntity<Phase> createPhase(@RequestBody Phase phase) {
        Phase savedPhase = phaseService.saveOrUpdatePhase(phase);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPhase);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Phase> getPhaseById(@PathVariable Long id) {
        Optional<Phase> phase = phaseService.findPhaseById(id);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhase(@PathVariable Long id) {
        phaseService.deletePhase(id);
        return ResponseEntity.noContent().build();
    }

}

package com.kahlab.easytask.controller;


import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.service.CollaboratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/collaborators")
public class CollaboratorController {

    @Autowired
    private CollaboratorService collaboratorService;

    @PostMapping
    public ResponseEntity<Collaborator> createCollaborator(@RequestBody Collaborator collaborator) {
        Collaborator savedCollaborator = collaboratorService.saveOrUpdateCollaborator(collaborator);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollaborator);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collaborator> getCollaboratorById(@PathVariable Long id) {
        Optional<Collaborator> collaborator = collaboratorService.findCollaboratorById(id);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollaborator(@PathVariable Long id) {
        collaboratorService.deleteCollaborator(id);
        return ResponseEntity.noContent().build();
    }

}

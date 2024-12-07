package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.repository.CollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollaboratorService {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    // Método para salvar um colaborador
    public Collaborator saveOrUpdateCollaborator(Collaborator collaborator) {
        return collaboratorRepository.save(collaborator);
    }

    // Atualizar colaborador
    public Collaborator updateCollaborator(Long id, Collaborator updatedCollaborator) {
        return collaboratorRepository.findById(id).map(existingCollaborator -> {
            existingCollaborator.setName(updatedCollaborator.getName());
            existingCollaborator.setPassword(updatedCollaborator.getPassword());
            existingCollaborator.setEmail(updatedCollaborator.getEmail());
            existingCollaborator.setPhone(updatedCollaborator.getPhone());
            existingCollaborator.setPosition(updatedCollaborator.getPosition());
            return collaboratorRepository.save(existingCollaborator);
        }).orElseThrow(() -> new RuntimeException("Collaborator not found with ID: " + id));
    }

    // Método para buscar colaborador pelo Nome
    public List<Collaborator> findCollaboratorByName(String name) {
        return collaboratorRepository.findByName(name);
    }

    // Método para buscar colaborador pela funcao//position
    public List<Collaborator> findCollaboratorByPosition(String position) {
        return collaboratorRepository.findByPosition(position);
    }

    // Método para buscar colaborador por ID
    public Optional<Collaborator> findCollaboratorById(Long id) {
        return collaboratorRepository.findById(id);
    }

    // Método para listar todos os colaboradores
    public List<Collaborator> findAllCollaborators() {
        return collaboratorRepository.findAll();
    }

    // Método para deletar um colaborador pelo ID
    public void deleteCollaborator(Long id) {
        collaboratorRepository.deleteById(id);
    }

}

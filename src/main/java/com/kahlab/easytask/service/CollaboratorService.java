package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.PasswordChangeDTO;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.repository.CollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollaboratorService {

    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LogService logService;

    private String getLoggedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Collaborator saveOrUpdateCollaborator(Collaborator collaborator) {
        boolean isNew = (collaborator.getIdCollaborator() == null);

        if (collaborator.getPassword() != null && !collaborator.getPassword().startsWith("$2a$")) {
            String hashedPassword = passwordEncoder.encode(collaborator.getPassword());
            collaborator.setPassword(hashedPassword);
        }

        Collaborator saved = collaboratorRepository.save(collaborator);

        // ✅ Obter colaborador autenticado (quem está criando/editando)
        String loggedEmail = getLoggedUserEmail();
        Collaborator executor = collaboratorRepository.findByEmail(loggedEmail)
                .orElseThrow(() -> new RuntimeException("Colaborador autenticado não encontrado"));

        // 📝 Registro de log
        String action = isNew ? "CREATE" : "UPDATE";
        logService.logAction(
                executor.getIdCollaborator(),
                "COLLABORATOR",
                action,
                "Colaborador '" + saved.getName() + "' foi " + (isNew ? "cadastrado" : "atualizado")
        );

        return saved;
    }

    // Atualizar colaborador
    public Collaborator updateCollaborator(Long id, Collaborator updatedCollaborator) {
        return collaboratorRepository.findById(id).map(existingCollaborator -> {
            existingCollaborator.setName(updatedCollaborator.getName());
            existingCollaborator.setEmail(updatedCollaborator.getEmail());
            existingCollaborator.setPhone(updatedCollaborator.getPhone());
            existingCollaborator.setPosition(updatedCollaborator.getPosition());

            Collaborator saved = collaboratorRepository.save(existingCollaborator);

            String loggedEmail = getLoggedUserEmail();
            Collaborator executor = collaboratorRepository.findByEmail(loggedEmail)
                    .orElseThrow(() -> new RuntimeException("Colaborador autenticado não encontrado"));

            logService.logAction(
                    executor.getIdCollaborator(),
                    "COLLABORATOR",
                    "UPDATE",
                    "Colaborador '" + saved.getName() + "' foi atualizado"
            );

            return saved;
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

    public void deleteCollaborator(Long id) {
        Collaborator toDelete = collaboratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        // 🔐 Obter quem está executando
        String loggedEmail = getLoggedUserEmail();
        Collaborator executor = collaboratorRepository.findByEmail(loggedEmail)
                .orElseThrow(() -> new RuntimeException("Colaborador autenticado não encontrado"));

        // 📝 Registrar log
        logService.logAction(
                executor.getIdCollaborator(),
                "COLLABORATOR",
                "DELETE",
                "Colaborador '" + toDelete.getName() + "' foi excluído"
        );

        collaboratorRepository.delete(toDelete);
    }

    public void changePassword(Long id, PasswordChangeDTO dto) {
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        // Verifica se a senha atual está correta
        if (!passwordEncoder.matches(dto.getCurrentPassword(), collaborator.getPassword())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Altera para a nova senha criptografada
        collaborator.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        collaboratorRepository.save(collaborator);

        // Log de alteração
        String loggedEmail = getLoggedUserEmail();
        Collaborator executor = collaboratorRepository.findByEmail(loggedEmail)
                .orElseThrow(() -> new RuntimeException("Colaborador autenticado não encontrado"));

        logService.logAction(
                executor.getIdCollaborator(),
                "COLLABORATOR",
                "PASSWORD_CHANGE",
                "Senha do colaborador '" + collaborator.getName() + "' foi alterada"
        );
    }


}

package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.PasswordChangeDTO;
import com.kahlab.easytask.model.AccessLevelEasyTask;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.repository.CollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
        boolean isFirstUser = collaboratorRepository.count() == 0;

        if (collaborator.getPassword() != null && !collaborator.getPassword().startsWith("$2a$")) {
            String hashedPassword = passwordEncoder.encode(collaborator.getPassword());
            collaborator.setPassword(hashedPassword);
        }

        // Se for o primeiro usuário, define como SUPERIOR
        if (isFirstUser) {
            collaborator.setPosition("SUPERIOR");
        }

        Collaborator saved = collaboratorRepository.save(collaborator);

        // Só registra o log se não for o primeiro usuário
        if (!isFirstUser) {
            try {
                String loggedEmail = getLoggedUserEmail();
                Optional<Collaborator> executorOpt = collaboratorRepository.findByEmail(loggedEmail);

                if (executorOpt.isPresent()) {
                    Collaborator executor = executorOpt.get();
                    String action = isNew ? "CREATE" : "UPDATE";
                    logService.logAction(
                            executor.getIdCollaborator(),
                            "COLLABORATOR",
                            action,
                            "Colaborador '" + saved.getName() + "' foi " + (isNew ? "cadastrado" : "atualizado")
                    );
                }
            } catch (Exception e) {
                // Se houver erro ao registrar o log, apenas ignora
                // O importante é que o colaborador foi salvo
            }
        }

        return saved;
    }

    // Atualizar colaborador
    public Collaborator updateCollaborator(Long id, Collaborator updatedCollaborator) {
        return collaboratorRepository.findById(id).map(existingCollaborator -> {
            // Atualizar campos básicos
            existingCollaborator.setName(updatedCollaborator.getName());
            existingCollaborator.setEmail(updatedCollaborator.getEmail());
            existingCollaborator.setPhone(updatedCollaborator.getPhone());
            existingCollaborator.setPosition(updatedCollaborator.getPosition());

            // Identificar colaborador autenticado
            String loggedEmail = getLoggedUserEmail();
            Collaborator executor = collaboratorRepository.findByEmail(loggedEmail)
                    .orElseThrow(() -> new RuntimeException("Colaborador autenticado não encontrado"));

            // Verificar se o nível de acesso será alterado
            if (updatedCollaborator.getAccessLevel() != null &&
                    !updatedCollaborator.getAccessLevel().equals(existingCollaborator.getAccessLevel())) {

                // Verifica se o executor tem permissão para alterar nível de acesso
                if (!executor.getAccessLevel().equals(AccessLevelEasyTask.SUPERIOR)) {
                    throw new AccessDeniedException("Apenas colaboradores com nível SUPERIOR podem alterar o nível de acesso.");
                }

                // Impede que o usuário altere seu próprio nível de acesso
                if (executor.getIdCollaborator().equals(existingCollaborator.getIdCollaborator())) {
                    throw new IllegalArgumentException("Você não pode alterar seu próprio nível de acesso.");
                }

                // Atualiza nível de acesso
                existingCollaborator.setAccessLevel(updatedCollaborator.getAccessLevel());
            }

            // Salva as alterações
            Collaborator saved = collaboratorRepository.save(existingCollaborator);

            // Log da ação
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

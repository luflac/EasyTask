package com.kahlab.easytask.controller;


import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.security.JwtUtil;
import com.kahlab.easytask.security.RefreshTokenService;
import com.kahlab.easytask.security.TokenBlackList;
import com.kahlab.easytask.service.CollaboratorService;
import com.kahlab.easytask.service.LogService;
import com.kahlab.easytask.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/collaborators")
public class CollaboratorController {

    @Autowired
    private CollaboratorService collaboratorService;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TokenBlackList tokenBlackList;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private LogService logService;


    @PreAuthorize("hasRole('SUPERIOR')")
    @PostMapping
    public ResponseEntity<Collaborator> createCollaborator(@RequestBody Collaborator collaborator) {
        Collaborator savedCollaborator = collaboratorService.saveOrUpdateCollaborator(collaborator);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollaborator);
    }

    @PreAuthorize("hasRole('SUPERIOR')")
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

    @PreAuthorize("hasRole('SUPERIOR')")
    @GetMapping
    public List<Collaborator> getAllCollaborators() {
        return collaboratorService.findAllCollaborators();
    }

    @PreAuthorize("hasRole('SUPERIOR')")
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

    @PostMapping("/register")
    public ResponseEntity<?> registerFirstCollaborator(@RequestBody Collaborator collaborator) {
        // Verifica se já existe colaborador no banco
        if (collaboratorRepository.count() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "O registro inicial já foi realizado. Acesso negado."));
        }

        // Define acesso como SUPERIOR
        collaborator.setAccessLevel(com.kahlab.easytask.model.AccessLevelEasyTask.SUPERIOR);

        // Salva o colaborador com senha criptografada
        Collaborator saved = collaboratorService.saveOrUpdateCollaborator(collaborator);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Primeiro colaborador registrado com sucesso.",
                        "email", saved.getEmail(),
                        "nome", saved.getName(),
                        "position", saved.getPosition(),
                        "accessLevel", saved.getAccessLevel().name()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String mail = request.get("email");
        String password = request.get("senha");

        Optional<Collaborator> collaboratorOpt = collaboratorRepository.findByEmail(mail);
        if (collaboratorOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Colaborador não encontrado"));
        }

        Collaborator collaborator = collaboratorOpt.get();

        if (!passwordEncoder.matches(password, collaborator.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Senha incorreta"));
        }

        // 🔐 Geração do access token
        String accessToken = jwtUtil.generateToken(
                collaborator.getEmail(),
                collaborator.getAccessLevel().name()
        );

        // 🔄 Geração do refresh token
        String refreshToken = java.util.UUID.randomUUID().toString();
        refreshTokenService.store(refreshToken, collaborator.getEmail(), Duration.ofDays(1));

        logService.logAction(
                collaborator.getIdCollaborator(),
                "AUTH",
                "LOGIN",
                "Login efetuado com sucesso pelo colaborador '" + collaborator.getName() + "'"
        );


        // ✅ Retorna os dois tokens
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "email", collaborator.getEmail(),
                "nome", collaborator.getName(),
                "position", collaborator.getPosition()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Valida o token
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(400).body(Map.of("error", "Token inválido"));
            }

            // Extrai email e busca colaborador
            String email = jwtUtil.extractEmail(token);
            Optional<Collaborator> collaboratorOpt = collaboratorRepository.findByEmail(email);
            if (collaboratorOpt.isPresent()) {
                Collaborator collaborator = collaboratorOpt.get();

                // ✅ REGISTRO DE LOG
                logService.logAction(
                        collaborator.getIdCollaborator(),
                        "AUTH",
                        "LOGOUT",
                        "Logout efetuado pelo colaborador '" + collaborator.getName() + "'"
                );
            }

            // Revoga o token
            tokenBlackList.revokeToken(token);

            return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Token não fornecido"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");


        if (!refreshTokenService.isValid(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token inválido ou expirado"));
        }

        String email = refreshTokenService.getEmail(refreshToken);

        Optional<Collaborator> colaborador = collaboratorRepository.findByEmail(email);
        if (colaborador.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuário não encontrado"));
        }

        String newAccessToken = jwtUtil.generateToken(email, colaborador.get().getAccessLevel().name());

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Token ausente ou inválido"));
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        Optional<Collaborator> colaborador = collaboratorRepository.findByEmail(email);
        if (colaborador.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Colaborador não encontrado"));
        }

        Collaborator c = colaborador.get();

        return ResponseEntity.ok(Map.of(
                "email", c.getEmail(),
                "nome", c.getName(),
                "position", c.getPosition(),
                "accessLevel", c.getAccessLevel().name()
        ));
    }


}


package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.CommentRequestDTO;
import com.kahlab.easytask.DTO.CommentResponseDTO;
import com.kahlab.easytask.mapper.CommentMapper;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.Comment;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.CommentRepository;
import com.kahlab.easytask.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class CommentService {

        private final EmailService emailService;
        private final CommentRepository commentRepository;
        private final TaskRepository taskRepository;
        private final CollaboratorRepository collaboratorRepository;
        private final CommentMapper commentMapper;

        public CommentResponseDTO createComment(CommentRequestDTO dto) {
            Task task = taskRepository.findById(dto.getIdTask())
                    .orElseThrow(() -> new EntityNotFoundException("Task not found"));

            Collaborator author = collaboratorRepository.findById(dto.getIdCollaborator())
                    .orElseThrow(() -> new EntityNotFoundException("Collaborator not found"));

            Comment comment = commentMapper.toEntity(dto, task, author);
            Comment saved = commentRepository.save(comment);

            // 🔔 Enviar e-mail ao responsável pela tarefa
            Collaborator taskOwner = task.getCollaborator();

            // Evitar notificar a si mesmo, se o autor for o dono da tarefa
            if (!taskOwner.getIdCollaborator().equals(author.getIdCollaborator())) {
                String emailBody = buildCommentEmail(
                        taskOwner.getName(),
                        task.getTitle(),
                        author.getName()
                );

                emailService.sendEmail(
                        taskOwner.getEmail(),
                        "Novo comentário na sua tarefa!",
                        emailBody
                );
            }

            return commentMapper.toDTO(saved);
        }

        public CommentResponseDTO updateComment(Long idComment, CommentRequestDTO dto) {
            Comment comment = commentRepository.findById(idComment)
                    .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado!"));

            // Obtém o e-mail do colaborador logado via SecurityContext
            String loggedEmail = getLoggedUserEmail();

            if (!comment.getCollaborator().getEmail().equalsIgnoreCase(loggedEmail)) {
                throw new SecurityException("Você só pode editar seus próprios comentários!.");
            }

            comment.setContent(dto.getContent());
            comment.setDateTime(LocalDateTime.now());

            Comment updated = commentRepository.save(comment);
            return commentMapper.toDTO(updated);
        }

        private String getLoggedUserEmail() {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            }
            return principal.toString(); // fallback
        }

        public List<CommentResponseDTO> getCommentsByTask(Long idTask) {
            List<Comment> comments = commentRepository.findByTaskIdTaskOrderByDateTimeAsc(idTask);
            return comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        public CommentResponseDTO getCommentById(Long idComment) {
            Comment comment = commentRepository.findById(idComment)
                    .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

            return commentMapper.toDTO(comment);
        }

        public List<CommentResponseDTO> getCommentsByCollaborator(Long idCollaborator) {
            List<Comment> comments = commentRepository.findByCollaboratorIdCollaboratorOrderByDateTimeDesc(idCollaborator);
            return comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        public void deleteComment(Long idComment) {
            Comment comment = commentRepository.findById(idComment)
                    .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado!"));

            String loggedEmail = getLoggedUserEmail();

            if (!comment.getCollaborator().getEmail().equalsIgnoreCase(loggedEmail)) {
                throw new SecurityException("Você só pode deletar seus comentários!");
            }

            commentRepository.delete(comment);
        }

        private String buildCommentEmail(String collaboratorName, String taskTitle, String commentAuthor) {
            return """
        <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2 style="color: #FF9800;">Novo comentário na sua tarefa</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p><strong>%s</strong> comentou na tarefa:</p>
                <blockquote style="border-left: 4px solid #FF9800; padding-left: 10px; color: #555;">
                    %s
                </blockquote>
                <p>Confira os detalhes no EasyTask.</p>
                <br/>
                <p style="font-size: 12px; color: #999;">Equipe EasyTask</p>
            </body>
        </html>
    """.formatted(collaboratorName, commentAuthor, taskTitle);
        }

    }
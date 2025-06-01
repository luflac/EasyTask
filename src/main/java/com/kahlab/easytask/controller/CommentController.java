package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.CommentRequestDTO;
import com.kahlab.easytask.DTO.CommentResponseDTO;
import com.kahlab.easytask.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // POST: Criar novo comentário
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO dto) {
        CommentResponseDTO response = commentService.createComment(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idComment}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long idComment,
            @RequestBody CommentRequestDTO dto) {
        CommentResponseDTO response = commentService.updateComment(idComment, dto);
        return ResponseEntity.ok(response);
    }

    // GET: Buscar todos os comentários de uma tarefa
    @GetMapping("/task/{idTask}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByTask(@PathVariable Long idTask) {
        List<CommentResponseDTO> comments = commentService.getCommentsByTask(idTask);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{idComment}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long idComment) {
        CommentResponseDTO comment = commentService.getCommentById(idComment);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/collaborator/{idCollaborator}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByCollaborator(@PathVariable Long idCollaborator) {
        List<CommentResponseDTO> comments = commentService.getCommentsByCollaborator(idCollaborator);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{idComment}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long idComment) {
        commentService.deleteComment(idComment);
        return ResponseEntity.noContent().build();
    }

}

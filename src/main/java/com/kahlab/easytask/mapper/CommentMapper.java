package com.kahlab.easytask.mapper;

import com.kahlab.easytask.DTO.CommentRequestDTO;
import com.kahlab.easytask.DTO.CommentResponseDTO;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.model.Comment;
import com.kahlab.easytask.model.Task;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDTO dto, Task task, Collaborator collaborator) {
        return Comment.builder()
                .content(dto.getContent())
                .task(task)
                .collaborator(collaborator)
                .build();
    }

    public CommentResponseDTO toDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .idComment(comment.getIdComment())
                .content(comment.getContent())
                .dateTime(comment.getDateTime())
                .collaboratorName(comment.getCollaborator().getName())
                .build();
    }
}

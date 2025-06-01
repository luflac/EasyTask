package com.kahlab.easytask.DTO;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private String content;
    private Long idTask;
    private Long idCollaborator;
}
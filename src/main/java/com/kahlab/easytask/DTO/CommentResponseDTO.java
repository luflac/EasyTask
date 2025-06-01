package com.kahlab.easytask.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDTO {
    private Long idComment;
    private String content;
    private LocalDateTime dateTime;
    private String collaboratorName;
}

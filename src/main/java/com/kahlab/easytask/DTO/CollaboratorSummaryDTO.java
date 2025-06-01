package com.kahlab.easytask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollaboratorSummaryDTO {
    private Long id;
    private String name;
    private String email;
}
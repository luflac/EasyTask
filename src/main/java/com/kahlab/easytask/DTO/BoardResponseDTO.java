package com.kahlab.easytask.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BoardResponseDTO {
    private Long id;
    private String name;
    private List<String> phases;
    private List<String> tasks;
    private List<String> collaborators;
}

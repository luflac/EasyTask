package com.kahlab.easytask.DTO;

import lombok.Data;

import java.awt.*;
import java.util.List;

@Data
public class BoardDTO {
    private String name;
    private List<Long> collaboratorIds;
    private List<Long> phaseIds;
}

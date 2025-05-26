package com.kahlab.easytask.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseBoardId implements Serializable {

    private Long phaseId;
    private Long boardId;

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof PhaseBoardId)) return false;
        PhaseBoardId that = (PhaseBoardId) o;
        return Objects.equals(phaseId, that.phaseId) && Objects.equals(boardId, that.boardId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(phaseId, boardId);
    }
}

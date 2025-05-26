package com.kahlab.easytask.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "phase_board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseBoard {

    @EmbeddedId
    private PhaseBoardId id;

    @ManyToOne
    @MapsId("phaseId")
    @JoinColumn(name = "id_phase")
    @JsonIgnore
    private Phase phase;


    @ManyToOne
    @MapsId("boardId")
    @JoinColumn(name = "id_board")
    @JsonIgnore
    private Board board;

}

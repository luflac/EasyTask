package com.kahlab.easytask.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Phase {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idPhase;

        private String name;
        private String description;
        private Integer sequence;

        @OneToMany(mappedBy = "phase")
        @JsonIgnore
        private List<Task> tasks;

}

package com.kahlab.easytask.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCollaborator;

    private String name;
    private String password;
    private String email;
    private String phone;
    private String position;//funçao do colaborador!

    @OneToMany(mappedBy = "collaborator")
    @JsonIgnore
    private List<Task> tasks;

}

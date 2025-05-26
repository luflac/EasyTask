package com.kahlab.easytask.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTask;

    @ManyToOne
    @JoinColumn(name = "id_board", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "id_collaborator", nullable = false)
    private Collaborator collaborator;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_phase", nullable = false)
    private Phase phase;

    private String title;
    private String description;
    private Short priority;

    private LocalDate dueDate;

    @Column(updatable = false)
    private LocalDate creationDate = LocalDate.now();

}

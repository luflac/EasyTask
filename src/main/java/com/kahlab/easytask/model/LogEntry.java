package com.kahlab.easytask.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "collaborator_id", nullable = false)
    private Collaborator collaborator;

    private String entityType; // TASK, CLIENT, COMMENT, etc.

    private String action;     // CREATE, UPDATE, DELETE, MOVE, etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime timestamp = LocalDateTime.now();

}


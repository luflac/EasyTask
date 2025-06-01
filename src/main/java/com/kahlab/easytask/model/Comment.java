package com.kahlab.easytask.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComment;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_task", nullable = false)
    private Task task;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_collaborator", nullable = false)
    private Collaborator collaborator;

    @PrePersist
    public void setDefaultDateTime() {
        if (this.dateTime == null) {
            this.dateTime = LocalDateTime.now();
        }
    }
}

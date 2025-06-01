package com.kahlab.easytask.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Client {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idClient;

        private String name;
        private String email;
        private String phone;

        @Column(length = 18)
        private String cnpj; // campo opcional

        @OneToMany(mappedBy = "client")
        @JsonIgnore
        private List<Task> tasks;

}

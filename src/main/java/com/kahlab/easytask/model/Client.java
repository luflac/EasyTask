package com.kahlab.easytask.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Client {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String email;
        private String phone;

        @OneToMany(mappedBy = "client")
        private List<Task> task;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getPhone() {
                return phone;
        }

        public void setPhone(String phone) {
                this.phone = phone;
        }

        public List<Task> getTask() {
                return task;
        }

        public void setTask(List<Task> task) {
                this.task = task;
        }
}

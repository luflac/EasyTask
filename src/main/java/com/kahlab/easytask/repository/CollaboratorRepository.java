package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    List<Collaborator> findByName(String name);
    List<Collaborator> findByPosition(String position);
    Optional<Collaborator> findByEmail(String email);

}

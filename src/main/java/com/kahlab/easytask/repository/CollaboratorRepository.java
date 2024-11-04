package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    List<Collaborator> findByName(String name);
    List<Collaborator> findByPosition(String position);

}

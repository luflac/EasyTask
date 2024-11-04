package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
    Optional<Phase> findByName(String name);
}

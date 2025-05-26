package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.PhaseBoard;
import com.kahlab.easytask.model.PhaseBoardId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseBoardRepository extends JpaRepository<PhaseBoard, PhaseBoardId> {
}

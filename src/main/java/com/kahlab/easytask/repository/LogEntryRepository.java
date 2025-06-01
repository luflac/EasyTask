package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByCollaborator_IdCollaborator(Long idCollaborator);
}

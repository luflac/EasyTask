package com.kahlab.easytask.service;

import com.kahlab.easytask.model.Phase;
import com.kahlab.easytask.repository.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhaseService {
    @Autowired
    private PhaseRepository phaseRepository;

    // Salva ou atualiza uma fase
    public Phase saveOrUpdatePhase(Phase phase) {
        return phaseRepository.save(phase);
    }

    // Busca uma fase pelo ID
    public Optional<Phase> findPhaseById(Long id) {
        return phaseRepository.findById(id);
    }

    // Lista todas as fases
    public List<Phase> findAllPhases() {
        return phaseRepository.findAll();
    }

    // Deleta uma fase pelo ID
    public void deletePhase(Long id) {
        phaseRepository.deleteById(id);
    }

    // Busca uma fase pelo nome
    public Optional<Phase> findPhaseByName(String name) {
        return phaseRepository.findByName(name);
    }
}

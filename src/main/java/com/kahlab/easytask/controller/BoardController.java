package com.kahlab.easytask.controller;

import com.kahlab.easytask.DTO.BoardDTO;
import com.kahlab.easytask.DTO.BoardPhaseDTO;
import com.kahlab.easytask.DTO.BoardResponseDTO;
import com.kahlab.easytask.model.*;
import com.kahlab.easytask.repository.BoardRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.PhaseBoardRepository;
import com.kahlab.easytask.repository.PhaseRepository;
import com.kahlab.easytask.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private PhaseRepository phaseRepository;
    @Autowired
    private PhaseBoardRepository phaseBoardRepository;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardDTO boardDTO) {
        Board board = boardService.createBoard(boardDTO);
        return ResponseEntity.ok(board);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return ResponseEntity.ok(boardService.toDTOList(boards));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> uptadeBoard(@PathVariable Long id , @RequestBody BoardDTO boardDTO) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Board board = optionalBoard.get();
        board.setName(boardDTO.getName());

        if (boardDTO.getCollaboratorIds() != null && !boardDTO.getCollaboratorIds().isEmpty()) {
            List<Collaborator> collaborators = collaboratorRepository.findAllById(boardDTO.getCollaboratorIds());
            board.setCollaborators(collaborators);
        }

        return ResponseEntity.ok(boardRepository.save(board));
    }

    @PostMapping("/{boardId}/collaborators/{collaboratorId}")
    public ResponseEntity<BoardResponseDTO> addCollaboratorToBoard(@PathVariable Long boardId, @PathVariable Long collaboratorId) {
        BoardResponseDTO dto = boardService.addCollaboratorToBoard(boardId, collaboratorId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{boardId}/collaborators/{collaboratorId}")
    public ResponseEntity<BoardResponseDTO> removeCollaboratorFromBoard(@PathVariable Long boardId, @PathVariable Long collaboratorId) {
        BoardResponseDTO dto = boardService.removeCollaboratorFromBoard(boardId, collaboratorId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{idBoard}/phases")
    public ResponseEntity<Void> addPhasesToBoard(@PathVariable Long idBoard, @RequestBody BoardPhaseDTO dto) {
        Optional<Board> boardOptional = boardRepository.findById(idBoard);
        if (boardOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Board board = boardOptional.get();
        List<Phase> phases = phaseRepository.findAllById(dto.getPhaseIds());

        for (Phase phase : phases) {
            PhaseBoardId id = new PhaseBoardId(phase.getIdPhase(), board.getId());
            PhaseBoard pb = new PhaseBoard(id, phase, board);
            phaseBoardRepository.save(pb);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}/phases/{phaseId}")
    public ResponseEntity<BoardResponseDTO> removePhaseFromBoard(@PathVariable Long boardId, @PathVariable Long phaseId) {
        BoardResponseDTO dto = boardService.removePhaseFromBoard(boardId, phaseId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

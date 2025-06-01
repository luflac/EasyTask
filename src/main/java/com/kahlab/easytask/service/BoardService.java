package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.BoardDTO;
import com.kahlab.easytask.DTO.BoardResponseDTO;
import com.kahlab.easytask.model.Board;
import com.kahlab.easytask.model.PhaseBoardId;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.repository.BoardRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import com.kahlab.easytask.repository.PhaseBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private PhaseBoardRepository phaseBoardRepository;

    public Board createBoard(BoardDTO dto) {
        Board board = new Board();
        board.setName(dto.getName());

        if (dto.getCollaboratorIds() != null && !dto.getCollaboratorIds().isEmpty()){
            List<Collaborator> collaborators = collaboratorRepository.findAllById(dto.getCollaboratorIds());
            board.setCollaborators(collaborators);
        }
        return boardRepository.save(board);
    }

    public BoardResponseDTO getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board não encontrado"));
        return toDTO(board);
    }

    public BoardResponseDTO addCollaboratorToBoard(Long boardId, Long collaboratorId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board não encontrado"));

        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        if (!board.getCollaborators().contains(collaborator)) {
            board.getCollaborators().add(collaborator);
            boardRepository.save(board);
        }

        return toDTO(board);
    }

    public BoardResponseDTO removeCollaboratorFromBoard(Long boardId, Long collaboratorId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board não encontrado"));

        Collaborator collaborator = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        board.getCollaborators().remove(collaborator);
        boardRepository.save(board);

        return toDTO(board);
    }

    public BoardResponseDTO removePhaseFromBoard(Long boardId, Long phaseId) {
        PhaseBoardId id = new PhaseBoardId(phaseId, boardId);

        if (!phaseBoardRepository.existsById(id)) {
            throw new RuntimeException("Fase não está vinculada a esse quadro.");
        }

        phaseBoardRepository.deleteById(id);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board não encontrado"));

        return toDTO(board);
    }

    public BoardResponseDTO toDTO(Board board) {
        // Fases vinculadas (PhaseBoard)
        List<String> phaseNames = board.getPhases().stream()
                .map(pb -> pb.getPhase().getName())
                .toList();

        // Títulos das tarefas do board
        List<String> taskTitles = board.getTasks().stream()
                .map(Task::getTitle)
                .toList();

        // Nomes dos colaboradores
        List<String> collaboratorNames = board.getCollaborators().stream()
                .map(Collaborator::getName)
                .toList();

        return BoardResponseDTO.builder()
                .id(board.getId())
                .name(board.getName())
                .phases(phaseNames)
                .tasks(taskTitles)
                .collaborators(collaboratorNames)
                .build();
    }

    public List<BoardResponseDTO> toDTOList(List<Board> boards) {
        return boards.stream()
                .map(this::toDTO)
                .toList();
    }




}

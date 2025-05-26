package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.BoardDTO;
import com.kahlab.easytask.model.Board;
import com.kahlab.easytask.model.Collaborator;
import com.kahlab.easytask.repository.BoardRepository;
import com.kahlab.easytask.repository.CollaboratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    public Board createBoard(BoardDTO dto) {
        Board board = new Board();
        board.setName(dto.getName());

        if (dto.getCollaboratorIds() != null && !dto.getCollaboratorIds().isEmpty()){
            List<Collaborator> collaborators = collaboratorRepository.findAllById(dto.getCollaboratorIds());
            board.setCollaborators(collaborators);
        }
        return boardRepository.save(board);
    }
}

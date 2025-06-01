package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTaskIdTaskOrderByDateTimeAsc(Long idTask);
    List<Comment> findByCollaboratorIdCollaboratorOrderByDateTimeDesc(Long idCollaborator);

}

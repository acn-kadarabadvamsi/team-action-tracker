package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.ActionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionCommentRepository
        extends JpaRepository<ActionComment, Long> {

    List<ActionComment>
    findByActionItem_ActionItemIdOrderByCreatedAtAsc(
            Long actionItemId);
}
package com.teamactiontracker.backend.repository;

import com.teamactiontracker.backend.model.ActionComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionCommentRepository extends JpaRepository<ActionComment, Long> {
}
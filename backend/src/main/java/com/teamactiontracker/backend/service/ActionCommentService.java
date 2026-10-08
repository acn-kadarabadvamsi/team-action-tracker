package com.teamactiontracker.backend.service;

import com.teamactiontracker.backend.model.ActionComment;
import com.teamactiontracker.backend.repository.ActionCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionCommentService {

    private final ActionCommentRepository actionCommentRepository;

    public ActionCommentService(ActionCommentRepository actionCommentRepository) {
        this.actionCommentRepository = actionCommentRepository;
    }

    public List<ActionComment> getComments() {
        return actionCommentRepository.findAll();
    }

    public ActionComment addComment(ActionComment comment) {
        return actionCommentRepository.save(comment);
    }
}
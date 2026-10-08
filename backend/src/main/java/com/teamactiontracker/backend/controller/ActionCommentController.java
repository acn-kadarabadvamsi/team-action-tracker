package com.teamactiontracker.backend.controller;

import com.teamactiontracker.backend.model.ActionComment;
import com.teamactiontracker.backend.service.ActionCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class ActionCommentController {

    private final ActionCommentService actionCommentService;

    public ActionCommentController(ActionCommentService actionCommentService) {
        this.actionCommentService = actionCommentService;
    }

    @GetMapping
    public List<ActionComment> getComments() {
        return actionCommentService.getComments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActionComment addComment(@RequestBody ActionComment comment) {
        return actionCommentService.addComment(comment);
    }
}
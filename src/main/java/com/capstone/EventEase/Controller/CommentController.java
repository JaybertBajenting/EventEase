package com.capstone.EventEase.Controller;


import com.capstone.EventEase.Entity.Comment;
import com.capstone.EventEase.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("addComment/")
    public ResponseEntity<?> comment(@RequestBody Comment comment){
        return ResponseEntity.ok(commentService.saveComment(comment));
    }

    @GetMapping("getCommentsByEventId/{eventId}")
    public ResponseEntity<?> getCommentsByEventId(@PathVariable Long eventId){
        return ResponseEntity.ok(commentService.getAllCommentsByEventId(eventId));
    }
}

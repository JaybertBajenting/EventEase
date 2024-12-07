package com.capstone.EventEase.Service;

import com.capstone.EventEase.Entity.Comment;
import com.capstone.EventEase.Repository.CommentRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;


    public List<Comment> getAllComments(){
        return commentRepository.findAll();
    }


    public Comment saveComment(Comment comment){
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsByEventId(Long eventId){
        return commentRepository.findAllByEventId(eventId);
    }





}

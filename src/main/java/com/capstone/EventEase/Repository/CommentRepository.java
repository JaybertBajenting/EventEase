package com.capstone.EventEase.Repository;

import com.capstone.EventEase.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByEventId(Long eventId);
}

package com.capstone.EventEase.Repository;

import java.util.List;

import com.capstone.EventEase.Entity.Event;
import com.capstone.EventEase.Entity.User;
import com.capstone.EventEase.Entity.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    List<UserEvent> findByEventId(Long eventId);
    List<UserEvent> findByUserId(Long userId);

    <Optional> UserEvent findByUserAndEvent(User user, Event event);
}



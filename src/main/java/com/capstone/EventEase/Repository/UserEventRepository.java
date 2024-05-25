package com.capstone.EventEase.Repository;

import java.util.List;
import com.capstone.EventEase.Entity.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    List<UserEvent> findByEventId(Long eventId);
}

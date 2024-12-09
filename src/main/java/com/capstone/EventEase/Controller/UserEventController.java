package com.capstone.EventEase.Controller;


import com.capstone.EventEase.Exceptions.DoubleJoinException;
import com.capstone.EventEase.Exceptions.EventFullException;
import com.capstone.EventEase.Exceptions.GenderNotAllowedException;
import com.capstone.EventEase.Exceptions.UserBlockedException;
import com.capstone.EventEase.Service.UserEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userevent")
@Tag(name = "USER EVENT CONTROLLER",description = "THIS IS WHERE THE USER EVENT CONTROLLERS ARE")
public class UserEventController {



        private  final UserEventService userEventService;




        @Operation(summary = "User can join event by passing userid and event id respectively")
        @PostMapping("/joinEvent/{userId}/{eventId}")
        public ResponseEntity<?> joinEvent(@PathVariable Long userId, @PathVariable Long eventId){
                try{
                    return new ResponseEntity<>(userEventService.joinEvent(userId,eventId),HttpStatus.OK);
                }catch (UserBlockedException | EventFullException | DoubleJoinException | GenderNotAllowedException |
                        EntityNotFoundException | DateTimeException e){
                    return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
                }
        }




        @DeleteMapping("/unjoinEvent/{userId}/{eventId}")
        public ResponseEntity<?> unjoinEvent(@PathVariable Long userId, @PathVariable long eventId){
            try{
                return new ResponseEntity<>(userEventService.unjoinEvent(userId,eventId),HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
            }
        }


      //  @Tag(name = "GET")
        @Operation(summary = "Get All Events Joined By User by passing a UserId")
        @GetMapping("/getAllEventsJoinedByUser/{userId}")
        public ResponseEntity<?> getAllEventsJoinedByUser(@PathVariable Long userId){
            try{
               return new ResponseEntity<>(userEventService.getAllEventsJoinedByUser(userId),HttpStatus.OK);
            }catch (EntityNotFoundException e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
            }catch (Exception e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
            }
        }


     //   @Tag(name = "GET")
        @Operation(summary = "Get All Users Joined To Event By Passing an EventId")
        @GetMapping("/getAllUsersJoinedToEvent/{eventId}")
    public ResponseEntity<?> getAllUsersJoinedToEvent(@PathVariable Long eventId){
        try{
            return new ResponseEntity<>(userEventService.getAllUsersJoinedToEvent(eventId),HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

}

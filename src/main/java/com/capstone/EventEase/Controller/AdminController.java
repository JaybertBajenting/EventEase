
package com.capstone.EventEase.Controller;


import com.capstone.EventEase.Entity.EventAttendance;
import com.capstone.EventEase.Entity.UserAttendance;
import com.capstone.EventEase.Exceptions.AttendanceCheckedException;
import com.capstone.EventEase.Exceptions.UserNotJoinedToAnEventException;
import com.capstone.EventEase.Service.AdminService;
import com.capstone.EventEase.Service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
@Tag(name = "ADMIN CONTROLLER", description = "THIS IS THE ADMIN CONTROLLER")
public class AdminController {



    private final AttendanceService attendanceService;

    private final AdminService adminService;
    @Operation(summary = "Check The Attendance of the User")
    @PostMapping("/attend/{eventId}/{username}/")
    public ResponseEntity<?> attendUsers(@PathVariable Long eventId, @PathVariable String username,@RequestParam("attendanceDate") @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME)OffsetDateTime attendanceDate){
        try{
            return new ResponseEntity<>(attendanceService.checkAttendance(eventId,username,attendanceDate),HttpStatus.OK);
        }catch (EntityNotFoundException e){ 
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }



    @Operation(summary = "Timeout User")
    @PostMapping("/timeout/{eventId}/{username}/")
    public ResponseEntity<?> timeoutUsers(@PathVariable Long eventId, @PathVariable String username,@RequestParam("timeoutDate") @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME)OffsetDateTime timeoutDate){
        try{
            return new ResponseEntity<>(attendanceService.checkTimeout(eventId,username,timeoutDate),HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Check if The User Attended the Event")
    @PostMapping("/checkAttend/{eventId}/{userId}")
    public ResponseEntity<?> checkIfUserAttended(@PathVariable Long eventId, @PathVariable Long userId){
        try{
            return new ResponseEntity<>(attendanceService.checkIfAttendedEvent(eventId,userId),HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "This will Block The User")
    @PostMapping("/block/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable Long userId){
            try{
                return new ResponseEntity<>(adminService.blockUser(userId),HttpStatus.OK);
            }catch (EntityNotFoundException e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
            }catch (Exception e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
            }
    }

    @Operation(summary = "This will UnBlock The User")
    @PostMapping("/unblock/{userId}")
    public ResponseEntity<?> unBlockUser(@PathVariable Long userId){
        try{
            return new ResponseEntity<>(adminService.unBlockUser(userId),HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }





    /*
    @Operation(summary = "Get The Number of Attended by UserId")
    @GetMapping("/getAttendance/{userId}")
    public int getAttendanceCount(@PathVariable Long userId){
        return attendanceService.getNumberofAttendance(userId);
    }

     */




    @Operation(summary = "Get The Number of Attended by UserId")
    @GetMapping("/getTopThree")
    public ResponseEntity<List<UserAttendance>> getTopThree() {
        List<UserAttendance> topThreeUsers = attendanceService.getTopThreeUsersByAttendance();
        return ResponseEntity.ok(topThreeUsers);
    }


    @Operation(summary = "Get the number of attendees in each event")
    @GetMapping("/getAttendanceByAllEvents")
    public ResponseEntity<List<EventAttendance>> eventAttendances(){
        List<EventAttendance> eventAttendances = attendanceService.getAttendanceOfUsersInAllEvents();
        return ResponseEntity.ok(eventAttendances);
    }


    @Operation(summary = "Get The User if it fits all the criterias")
    @GetMapping("/getUserByUsername/{eventId}/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable Long eventId, @PathVariable String username){
            try{
                return new ResponseEntity<>(attendanceService.verifyUser(eventId,username),HttpStatus.OK);
            }catch (UserNotJoinedToAnEventException e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
            }catch (AttendanceCheckedException e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
            }catch (Exception e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
            }
    }


    @Operation(summary = "Get How Many Days an Event is Going")
    @GetMapping("/getCountDaysEvent/{eventId}")
    public ResponseEntity<?> getCountDaysEvent(@PathVariable Long eventId){
        return new ResponseEntity<>(attendanceService.countDays(eventId),HttpStatus.OK);
    }



    @Operation(summary = "Counter Attendance")
    @GetMapping("/count/{eventId}")
    public ResponseEntity<?> counterAttendance(@PathVariable Long eventId){
        return new ResponseEntity<>(attendanceService.counterAttendance(eventId),HttpStatus.OK);
    }




    @Operation(summary = "Get All Users Joined To Event After Attendance")
    @GetMapping("/getUsersJoinedAfterAttendance/{eventId}")
    public ResponseEntity<?> getUsersJoinedAfterAttending(@PathVariable Long eventId){

        try{
           return new ResponseEntity<>(attendanceService.getAllUsersJoinedToEventAfterAttendance(eventId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }



    @Operation(summary = "Get All Events Joined By Event After Attendance")
    @GetMapping("/getEventsJoinedAttendance/{userId}")
    public ResponseEntity<?> getEventsJoinedAfterAttending(@PathVariable Long userId){

        try{
            return new ResponseEntity<>(attendanceService.getAllEventsJoinedByUserAfterAttendance(userId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}







package com.capstone.EventEase.Controller;



import com.capstone.EventEase.Entity.Event;
import com.capstone.EventEase.Exceptions.DoubleJoinException;
import com.capstone.EventEase.Exceptions.EventFullException;
import com.capstone.EventEase.Exceptions.GenderNotAllowedException;
import com.capstone.EventEase.Exceptions.UserBlockedException;
import com.capstone.EventEase.Service.EventService;
import com.capstone.EventEase.Service.ImageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
@Tag(name = "EVENT CONTROLLER", description = "THIS IS THE EVENT CONTROLLER")
public class EventController {

    private final EventService eventService;

    private final ImageService imageService;




    @GetMapping("/event/getEventNow")
    public ResponseEntity<?> getEventNow(){
        return ResponseEntity.ok(eventService.getEventByNow());
    }









    @Operation(summary = "CREATE AN EVENT")
    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(@RequestParam("creator") String username,@RequestBody Event event) throws GenderNotAllowedException, DoubleJoinException, EventFullException, UserBlockedException
    ,EntityNotFoundException, DateTimeException  {
        try{
            return new ResponseEntity<>(eventService.createEvent(username,event),HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }



/*
    @Operation(summary = "CREATE AN EVENT")
    @PostMapping(value = "/createEvent", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createEvent(@RequestParam("createdBy") String username,
                                         @RequestParam("event") String eventJson,
                                         @RequestParam("eventImage") MultipartFile file,
                                         @RequestParam("eventName") String eventName,
                                         @RequestParam("eventDescription") String eventDescription,
                                         @RequestParam("eventStarts") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime eventStarts,
                                         @RequestParam("eventEnds") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime eventEnds,
                                         @RequestParam("location") String location,
                                         @RequestParam("eventLimit") Integer eventLimit,
                                         @RequestParam("allowedGender") AllowedGender allowedGender,
                                         @RequestParam("eventType") String eventType,
                                         @RequestParam(value = "usersLiked", required = false) Set<String> usersLiked,
                                         @RequestParam(value = "usersDisliked", required = false) Set<String> usersDisliked,
                                         @RequestParam(value = "preRegisteredUsers", required = false) Set<String> preRegisteredUsers)
            throws GenderNotAllowedException, DoubleJoinException, EventFullException, UserBlockedException, EntityNotFoundException, DateTimeException, IOException {
        try {
            // Convert eventJson to Event object
            ObjectMapper objectMapper = new ObjectMapper();
            Event event = objectMapper.readValue(eventJson, Event.class);

            // Set the image and other fields
            event.setEventPicture(file.getBytes());
            event.setEventName(eventName);
            event.setEventDescription(eventDescription);
            event.setEventStarts(eventStarts);
            event.setEventEnds(eventEnds);
            event.setLocation(location);
            event.setEventLimit(eventLimit);
            event.setAllowedGender(allowedGender);
            event.setEventType(eventType);

            // Set usersLiked, usersDisliked, and preRegisteredUsers if provided
            if (usersLiked != null) {
                event.setUsersLiked(usersLiked);
            }
            if (usersDisliked != null) {
                event.setUsersDisliked(usersDisliked);
            }
            if (preRegisteredUsers != null) {
                event.setPreRegisteredUsers(preRegisteredUsers);
            }

            // Call the service to create the event
            return new ResponseEntity<>(eventService.createEvent(username, event), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("messages", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


 */



    @Operation(summary = "Update Event Picture by Passing EventId and New Event Picture")
    @PutMapping("/updateEventPicture/{eventId}")
    public ResponseEntity<?> uploadEventPicture(@PathVariable Long eventId,@RequestParam("eventImage") MultipartFile file) throws IOException{
        return new ResponseEntity<>(imageService.uploadImage(eventId,file,true),HttpStatus.OK);
    }




    @Operation(summary = "Update Event")
    @PutMapping("/updateEvent/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody Event event){
        try{
            return new ResponseEntity<>(eventService.updateEvent(eventId,event),HttpStatus.OK);
        }catch (EntityNotFoundException | DateTimeException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get Event By Passing EventId")
    @GetMapping("getEventById/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable Long eventId) throws Exception{
        return new ResponseEntity<>(eventService.getEvent(eventId),HttpStatus.OK);
    }



    @Operation(summary = "Get Event Picture by Passing Event Id")

    @GetMapping("/getEventPicture/{eventId}")
    public ResponseEntity<?> getEventPicture(@PathVariable Long eventId) throws IOException{

        String format = imageService.getPictureFormat(eventId,true);
        MediaType mediaType;

        switch (format) {
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "jpg":
            case "jpeg":
            case "jfif":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "bmp":
                mediaType = MediaType.valueOf("image/bmp");
                break;
            case "tiff":
                mediaType = MediaType.valueOf("image/tiff");
                break;
            case "webp":
                mediaType = MediaType.valueOf("image/webp");
                break;
            case "svg":
                mediaType = MediaType.valueOf("image/svg+xml");
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported Format: " + format);
        }

        byte[] eventImage = imageService.downloadImage(eventId,true);
        return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(eventImage);


    }




    @Operation(summary = "Get ALl Events")

    @GetMapping("/getAllEvents")
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }


    @Operation(summary = "Delete Event By Passing eventId")
    @DeleteMapping("deleteEventById/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) throws Exception {
       try{
           return new ResponseEntity<>(eventService.deleteEvent(eventId), HttpStatus.OK);
       }catch (EntityNotFoundException e){
           return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.NOT_FOUND);
       }
    }



    @Operation(summary = "Like the event")
    @PostMapping("/likeEvent/{eventId}/{userId}")
    public ResponseEntity<?> likeEvent(@PathVariable Long eventId,@PathVariable Long userId){
            try{
                return new ResponseEntity<>(eventService.likeEvent(eventId,userId),HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
            }
    }

    @Operation(summary = "Like the event")
    @PostMapping("/dislikeEvent/{eventId}/{userId}")
    public ResponseEntity<?> dislikeEvent(@PathVariable Long eventId,@PathVariable Long userId){
        try{
            return new ResponseEntity<>(eventService.dislikeEvent(eventId,userId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }




    @Operation(summary = "Get Event By Current Date")
    @GetMapping("/getEventByCurrentDate")
    public ResponseEntity<?> getEventByCurrentDate(@RequestParam("currentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime currentDate){
            try{
                return new ResponseEntity<>(eventService.getByEventStarts(currentDate),HttpStatus.OK);
            }catch (EntityNotFoundException e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.BAD_REQUEST);
            }catch (Exception e){
                return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
            }
    }


//
//    @PostMapping("/sendAllEmails")
//    public void sendAllEmails() throws JsonProcessingException {
//        eventService.sendAllEventsEmails();
//    }



    @GetMapping("/getStartByEventId/{eventId}")
    public ResponseEntity<?> getStartsByEvent(@PathVariable Long eventId){
        return ResponseEntity.ok(eventService.getEventStarts(eventId));
    }









@GetMapping("/greet")
    public String greet(){
        return "Hello";
    }


}


package com.capstone.EventEase.Controller;


import com.capstone.EventEase.Entity.Event;
import com.capstone.EventEase.Service.EventService;
import com.capstone.EventEase.Service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/event")
@Tag(name = "EVENT CONTROLLER", description = "THIS IS THE EVENT CONTROLLER")
public class EventController {

    private final EventService eventService;

    private final ImageService imageService;


    @Operation(summary = "CREATE AN EVENT")
    @PostMapping("createEvent")
    public ResponseEntity<?> createEvent(@RequestBody Event event){
      return new ResponseEntity(eventService.craeteEvent(event),HttpStatus.OK);
    }



    @Operation(summary = "Update Event By Passing Event Id With New Event Credentials")
    @PutMapping("updateEvent/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId,@RequestBody Event event){
        return new ResponseEntity(eventService.updateEvent(eventId,event),HttpStatus.OK);
    }



    @Operation(summary = "Update Event Picture by Passing EventId and New Event Picture")
    @PutMapping("/updateEventPicture/{eventId}")
    public ResponseEntity<?> uploadEventPicture(@PathVariable Long eventId,@RequestParam("eventImage") MultipartFile file) throws IOException{
        return new ResponseEntity<>(imageService.uploadEventImage(eventId,file),HttpStatus.OK);
    }




    @Operation(summary = "Get Event By Passing EventId")
    @GetMapping("getEventById/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable Long eventId) throws Exception{
        return new ResponseEntity<>(eventService.getEvent(eventId),HttpStatus.OK);
    }




    @Operation(summary = "Get Event Picture by Passing Event Id")

    @GetMapping("/getEventPicture/{eventId}")
    public ResponseEntity<?> getEventPicture(@PathVariable Long eventId) throws IOException{

        String format = imageService.getEventPictureFormat(eventId);
        MediaType mediaType;
        switch (format){
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "svg":
                mediaType = MediaType.valueOf("image/svg+xml");
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported Format: " + format);
        }
        byte[] eventImage = imageService.downloadEventImage(eventId);
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
        return new ResponseEntity<>(eventService.deleteEvent(eventId), HttpStatus.OK);
    }








}


package com.capstone.EventEase.Controller;


import com.capstone.EventEase.DTO.Request.ForgotPasswordRequest;
import com.capstone.EventEase.DTO.Request.LoginRequest;
import com.capstone.EventEase.DTO.Request.NewPasswordRequest;
import com.capstone.EventEase.DTO.Request.RegisterRequest;
import com.capstone.EventEase.DTO.Response.LoginResponse;
import com.capstone.EventEase.Entity.Event;
import com.capstone.EventEase.Entity.User;
import com.capstone.EventEase.Exceptions.AttendanceCheckedException;
import com.capstone.EventEase.Exceptions.UserNotJoinedToAnEventException;
import com.capstone.EventEase.Repository.EventRepository;
import com.capstone.EventEase.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
@Tag(name = "AUTHENTICATION CONTROLLER", description = "THIS IS THE AUTHENTICATION CONTROLLER")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final AttendanceService attendanceService;
    private final ImageService imageService;
    private final EventService eventService;



    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email){
        try{
            return new ResponseEntity<>(authenticationService.sendForgotPasswordToken(email),
            HttpStatus.OK);
        } catch (EntityExistsException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }



    


    @Operation(summary = "Get The User if it fits all the criterias")
    @GetMapping("/getUserByUuid/{eventId}/{uuid}")
    public ResponseEntity<?> getUserByUsername(@PathVariable Long eventId, @PathVariable String uuid){
        try{
            return new ResponseEntity<>(attendanceService.verifyUser(eventId,uuid),HttpStatus.OK);
        }catch (UserNotJoinedToAnEventException | AttendanceCheckedException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Counter Attendance")
    @GetMapping("/event/count/{eventId}")
    public ResponseEntity<?> counterAttendance(@PathVariable Long eventId){
        return new ResponseEntity<>(attendanceService.counterAttendance(eventId),HttpStatus.OK);
    }



  //  @Cacheable("general-cache")
    @GetMapping("/event/getAllEvents")
    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }



    @GetMapping("/event/getEventNow")
    public ResponseEntity<?> getEventNow(){
        return ResponseEntity.ok(eventService.getEventByNow());
    }




    @Operation(summary = "Check The Attendance of the User")
    @PostMapping("/attend/{eventId}/{uuid}/")
    public ResponseEntity<?> attendUsers(@PathVariable Long eventId, @PathVariable String uuid,@RequestParam("attendanceDate") @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME)OffsetDateTime attendanceDate){
        try{
            return new ResponseEntity<>(attendanceService.checkAttendance(eventId,uuid,attendanceDate),HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }




    @Operation(summary = "Timeout User")
    @PostMapping("/timeout/{eventId}/{uuid}/")
    public ResponseEntity<?> timeoutUsers(@PathVariable Long eventId, @PathVariable String uuid,@RequestParam("timeoutDate") @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME)OffsetDateTime timeoutDate){
        try{
            return new ResponseEntity<>(attendanceService.checkTimeout(eventId,uuid,timeoutDate),HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }






    // @Tag(name = "userGET")
    @Operation(summary = "Gets the users profile picture by sending an id")
    @GetMapping("/user/getProfilePicture/{userId}")
    public ResponseEntity<?> getProfilePicture(@PathVariable Long userId) throws IOException{

        String format = imageService.getPictureFormat(userId,false);
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
        byte[] profilePicture = imageService.downloadImage(userId,false);
        return ResponseEntity.status(HttpStatus.OK).contentType(mediaType).body(profilePicture);

    }









    @Operation(summary = "Get Event Picture by Passing Event Id")
    @GetMapping("event/getEventPicture/{eventId}")
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


    @PostMapping("/new-password")
    public ResponseEntity<?> newPassword(@RequestParam("token") String token, @RequestBody NewPasswordRequest newPasswordRequest){
      try{
          return ResponseEntity.ok(authenticationService.newPassword(token, newPasswordRequest.getNewPassword()));
      }catch (IllegalArgumentException e){
          return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.NOT_FOUND);
      }catch (Exception e){
          return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
      }
    }



    @PostMapping("/verifyToken")
    public ResponseEntity<Boolean> verifyToken(@RequestParam("token") String token){
        return ResponseEntity.ok(passwordResetTokenService.verifyToken(token));
    }



    @PostMapping("/verifyPassword/{userId}/{password}")
    public ResponseEntity<?> verifyPassword(@PathVariable Long userId, @PathVariable String password) {
        try {
            boolean result = authenticationService.verifyPassword(userId, password);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }







    @GetMapping("/hello")
    @Operation(summary = "Say Hello", description = "This will say hello")
    public ResponseEntity<String> greet(){
        return new ResponseEntity<>("Hello World",HttpStatus.OK);
    }







    @Operation(summary = "Register A User with Send Link")
    @PostMapping("/registerLink")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        try{
            return new ResponseEntity<>(authenticationService.generateConfirmationToken(registerRequest),HttpStatus.OK);
        }catch (EntityExistsException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (MessagingException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }




    /*

    @Operation(summary = "Confirm Account Creation")
    @GetMapping("/confirm/")
    public ResponseEntity<?> confirmRegister(@RequestParam("token") String token, HttpServletResponse response){
        try{
            boolean isConfirmed = authenticationService.confirmAccount(token);
            if(isConfirmed){
                response.sendRedirect("http://localhost:3000/Login");
            }
            return new ResponseEntity<>(isConfirmed,HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
     */


    @Operation(summary = "Confirm Account Creation")
    @GetMapping("/confirm/")
    public ResponseEntity<?> confirmRegister(@RequestParam("token") String token) {
        try {
            boolean isConfirmed = authenticationService.confirmAccount(token);
            if (isConfirmed) {
                // Return the confirmation HTML page
                String confirmationHtml = getConfirmationHtml();
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(confirmationHtml);
            }
            return ResponseEntity.badRequest().body("Invalid or expired token");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getConfirmationHtml() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Account Confirmed</title>
                <style>
                    body {
                        font-family: 'Arial', sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                        text-align: center;
                    }
                    .container {
                        background-color: #ffffff;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        padding: 40px;
                    }
                    h1 {
                        color: #FDCC01;
                    }
                    #timer {
                        font-size: 24px;
                        font-weight: bold;
                        margin: 20px 0;
                    }
                </style>
            </head>
            
          
            
            <body>
                <div class="container">
                    <h1>Account Confirmed!</h1>
                    <p>Your account has been successfully confirmed. Thank you for joining EventEase!</p>
                    <p>You will be redirected to the login page in <span id="timer">5</span> seconds.</p>
                </div>

                <script>
                    let timeLeft = 5;
                    const timerElement = document.getElementById('timer');

                    function updateTimer() {
                        timerElement.textContent = timeLeft;
                        if (timeLeft === 0) {
                            window.location.href = 'https://www.eventease.pro/Login';
                        } else {
                            timeLeft--;
                            setTimeout(updateTimer, 1000);
                        }
                    }

                    updateTimer();
                </script>
            </body>
            </html>
        """;
    }




    @Operation(summary = "Register A User")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        try{
            LoginResponse loginResponse = authenticationService.registerUser(registerRequest);
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        }catch (EntityExistsException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()), HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/test")
    public String greetings(){
        return "Welcome to the testing unit";
    }
    @Operation(summary = "Login With User Credentials")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        try{

            LoginResponse loginResponse = authenticationService.loginUser(loginRequest);
            return new ResponseEntity<>(loginResponse,HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.CONFLICT);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Calls Db")
    @GetMapping("/callCron")
    public ResponseEntity<?> callCron(){
        try{
            return new ResponseEntity<>(authenticationService.callCron(),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Map.of("messages",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }





}

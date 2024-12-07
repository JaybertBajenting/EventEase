package com.capstone.EventEase.Controller;


import com.capstone.EventEase.Entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/authenticated")
@Tag(name = "Authenticated Controller", description = "THIS IS THE AUTHENTICATED CONTROLLER")
public class AuthenticatedController {


    @Operation(summary = "Get the current User")
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser  = (User) authentication.getPrincipal();
            return ResponseEntity.ok(currentUser);
    }

}

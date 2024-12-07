package com.capstone.EventEase.DTO.Response;

import com.capstone.EventEase.Entity.User;
import lombok.Data;

import java.util.Base64;

@Data
public class LoginResponse {

    private String token;

    private User user;


    public LoginResponse(User user, String token){
        this.user = user;
        this.token = token;
    }






}

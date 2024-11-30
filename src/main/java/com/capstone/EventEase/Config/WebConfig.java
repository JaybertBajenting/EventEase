package com.capstone.EventEase.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;



@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer{


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","https://www.eventease.pro","https://eventease.pro","https://eventease-five.vercel.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE")
               .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
    }





}


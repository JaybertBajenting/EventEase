package com.capstone.EventEase.Config;


import com.capstone.EventEase.ENUMS.Role;
import com.capstone.EventEase.Service.UserService;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {





    private final UserService userService;


    private final JwtAuthenticationFilter jwtAuthenticationFilter;



    @Getter
    @Value("${app.url}")
    private String appUrl;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/v1/authenticated/**").hasAnyAuthority(Role.STUDENT.name(), Role.ADMIN.name())
                        //.requestMatchers("/swagger-ui/**", "/v3/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/swagger-ui/**", "/v3/**").permitAll()
                        .requestMatchers("/user").hasAnyAuthority(Role.STUDENT.name(), Role.ADMIN.name())
                        .requestMatchers("/admin").hasAnyAuthority(Role.ADMIN.name(), Role.STUDENT.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }







    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/v1/authenticated/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/**").permitAll()
                        .requestMatchers("/api/v1/auth/analytics/**").permitAll()
                        .requestMatchers("/api/v1/auth/user").hasAnyRole(Role.STUDENT.name())
                        .requestMatchers("/api/v1/auth/admin").hasAnyRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }




     */









    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }







    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl(getAppUrl());
        server.setDescription("EventEase");

        Contact contact = new Contact();
        contact.setName("Event Ease Team");
        contact.setEmail("EventEase@gmail.com");

        Info information = new Info().title("EventEase System API")
                .version("1,0")
                .description("This Api exposes the endpoints to Track Events")
                .contact(contact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
    }


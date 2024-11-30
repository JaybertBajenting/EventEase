package com.capstone.EventEase.Service;


import com.capstone.EventEase.DTO.Request.LoginRequest;
import com.capstone.EventEase.DTO.Request.RegisterRequest;
import com.capstone.EventEase.DTO.Response.LoginResponse;
import com.capstone.EventEase.ENUMS.Gender;
import com.capstone.EventEase.ENUMS.Role;
import com.capstone.EventEase.Entity.PasswordResetToken;
import com.capstone.EventEase.Entity.User;
import com.capstone.EventEase.Entity.VerificationToken;
import com.capstone.EventEase.Exceptions.AccountNotEnabledException;
import com.capstone.EventEase.Repository.PasswordResetTokenRepository;
import com.capstone.EventEase.Repository.UserRepository;
import com.capstone.EventEase.Repository.VerificationTokenRepository;
import com.capstone.EventEase.UTIL.ImageUtils;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.eclipse.angus.mail.smtp.SMTPAddressFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;



    private  final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;


    private final VerificationTokenService verificationTokenService;


    private final EmailService emailService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;


    private final VerificationTokenRepository verificationTokenRepository;


    private static final String DEFAULT_PROFILE_PICTURE_PATH = "static/images/profile.png";

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private byte[] getDefaultProfilePicture (){

        try{

            Path path = Paths.get(new ClassPathResource(DEFAULT_PROFILE_PICTURE_PATH).getURI());
            return Files.readAllBytes(path);
        }catch (IOException e){
            System.out.println("Error: " + e.getMessage());
            return new byte[0];
        }
    }




    private void checkIfUserExists(String username) throws MessagingException {
        if(userRepository.findByUsername(username) != null){
            User user = userRepository.findByUsername(username);
            VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
            if(verificationToken == null){
                throw new EntityExistsException("Username Already Exists hehe!");
            }else{
            String token = verificationToken.getToken();
            if(verificationTokenService.isVerificationTokenExpired(token)){
                emailService.sendConfirmationLink(username,token);
                verificationTokenRepository.delete(verificationToken);

                String newToken = UUID.randomUUID().toString();
                User oldUser = userRepository.findByUsername(username);
                generateVerificationToken(newToken,oldUser);
                throw new EntityExistsException("Check your email for confirmation");
            }else if(!verificationTokenService.isVerificationTokenExpired(token)){
                throw new EntityExistsException("Confirmation Link already sent to Gmail");
            }else{
                throw new EntityExistsException("Username Already Exists!");
            }
            }
        }
    }



    public LoginResponse registerUser(RegisterRequest registerRequest) throws MessagingException {
        checkIfUserExists(registerRequest.getUsername());
        User newUser = createUserFromRequest(registerRequest);
        User user = userRepository.save(newUser);
        return new LoginResponse(user, jwtService.generateToken(user));
    }

    private User createUserFromRequest(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.STUDENT)
                .uuid(UUID.randomUUID().toString())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .IdNumber(registerRequest.getIdNumber())
                .department(registerRequest.getDepartment())
                .isBlocked(false)
                .gender(Gender.valueOf(registerRequest.getGender().toString()))
                .isVerified(false)
                .profilePicture(ImageUtils.compressImage(getDefaultProfilePicture()))
                .profilePictureName("XyloGraph1.png")
                .profilePictureType("image/png")
                .build();
    }






    public String generateConfirmationToken(RegisterRequest registerRequest) throws MessagingException {
        checkIfUserExists(registerRequest.getUsername());
        User newUser = createUserFromRequest(registerRequest);
        User user = userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        generateVerificationToken(token, user);

        logger.info("Verification token generated for user: {}", user.getUsername());
        return "Check your email for verification.";
    }

    public void generateVerificationToken(String token, User user) throws MessagingException {
        try {
            VerificationToken verificationToken = new VerificationToken(token, user);
            verificationTokenRepository.save(verificationToken);
            emailService.sendConfirmationLink(user.getUsername(), token);

            logger.info("Confirmation email sent to: {}", user.getUsername());
        } catch (SMTPAddressFailedException e) {
            handleMessagingException("Invalid email address: " + user.getUsername(), e);
        } catch (MessagingException e) {
            throw new MessagingException("Invalid email address:  " + user.getUsername(), e);
        }
    }

    

    private void handleMessagingException(String message, Exception e) throws MessagingException {
        logger.error("{} - Error: {}", message, e.getMessage());
        throw new MessagingException(message, e);
    }



    public boolean confirmAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new EntityNotFoundException("Token Not Found!");
        }
        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return true;
    }



    public LoginResponse loginUser(LoginRequest loginRequest) throws AccountNotEnabledException {
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername());

            if (user == null) {
                throw new BadCredentialsException("Invalid username or password");
            }


            if (!user.isVerified()) {
                throw new AccountNotEnabledException("Account Has Not Been Verified");
            }

            authenticateUser(loginRequest);

            return new LoginResponse(user, jwtService.generateToken(user));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    private void authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );
    }



    public LoginResponse updateUser(Long userId, User user) throws IOException {
        User userPerson = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id: " + userId + " not found!")
        );

        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            userPerson.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            userPerson.setLastName(user.getLastName());
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            userPerson.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return new LoginResponse(userRepository.save(userPerson), jwtService.generateToken(userPerson));
    }



    public String sendForgotPasswordToken(String email) throws MessagingException {
        User user = userRepository.findByUsername(email);
        if (user == null) {
            throw new EntityNotFoundException("User with that email not found!");
        }

        PasswordResetToken existingUser = passwordResetTokenRepository.findByUser(user);
        if (existingUser != null) {
            throw new EntityExistsException("Token already sent");
        }

        String token = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        String randomToken = token.substring(0, 6);

        PasswordResetToken passwordResetToken = new PasswordResetToken(randomToken, user);
        passwordResetTokenRepository.save(passwordResetToken);

        emailService.forgotPasswordEmail(email, randomToken);
        return "Email has been sent!";
    }






    public String newPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        validatePasswordResetToken(passwordResetToken);

        User user = passwordResetToken.getUser();
        updateUserPassword(user, newPassword);

        passwordResetTokenRepository.delete(passwordResetToken);
        return "New Password Updated";
    }

    private void validatePasswordResetToken(PasswordResetToken passwordResetToken) {
        if (passwordResetToken == null) {
            throw new EntityNotFoundException("Invalid Token");
        }
        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new IllegalArgumentException("Token has expired!");
        }
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }






    public boolean verifyPassword(Long userId, String oldPassword){
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> passwordEncoder.matches(oldPassword, value.getPassword())).orElse(false);
    }


    public String callCron() {
       User user = userRepository.findByUsername("admin");
       if (user == null) {
           throw new EntityNotFoundException("User with that email not found!");
       }
      return "Cron Job Called";
    }



}

package com.capstone.EventEase.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_verification")
public class VerificationToken {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    private String token;


    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public VerificationToken(String token, User user){
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(30);
    }
}



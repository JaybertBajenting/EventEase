package com.capstone.EventEase.Entity;

import com.capstone.EventEase.ENUMS.AllowedGender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_event")
public class Event {

    /*
    implements Serializable
    @Serial
    private static final long serialVersionUID = 1L;
    */
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;

    @Column(length = 100000)
    private String eventDescription;


    private OffsetDateTime eventStarts;


    private int likes = 0;

    private int dislikes = 0;


    private Integer eventLimit;

    private int usersJoined = 0;



    @Enumerated(EnumType.STRING)
    private AllowedGender allowedGender;

    private Set<String> usersLiked = new HashSet<>();

    private Set<String> usersDisliked = new HashSet<>();

    private Set<String> preRegisteredUsers = new HashSet<>();

    private String createdBy;

    private OffsetDateTime eventEnds;



    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "BYTEA")
    private byte[] eventPicture;





    private String eventPictureType;

    private String eventPictureName;

    private List<String> department;

    private boolean doneSending = false;

    private String eventType;
}

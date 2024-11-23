package com.capstone.EventEase.Service;

import com.capstone.EventEase.Classes.PasswordGenerator;
import com.capstone.EventEase.DTO.Request.EmailSendRequestDTO;
import com.capstone.EventEase.ENUMS.Gender;
import com.capstone.EventEase.ENUMS.Role;
import com.capstone.EventEase.Entity.Event;
import com.capstone.EventEase.Entity.User;
import com.capstone.EventEase.Entity.UserEvent;
import com.capstone.EventEase.Exceptions.DoubleJoinException;
import com.capstone.EventEase.Exceptions.EventFullException;
import com.capstone.EventEase.Exceptions.GenderNotAllowedException;
import com.capstone.EventEase.Exceptions.UserBlockedException;
import com.capstone.EventEase.Repository.AttendanceRepository;
import com.capstone.EventEase.Repository.EventRepository;
import com.capstone.EventEase.Repository.UserEventRepository;

import com.capstone.EventEase.Repository.UserRepository;
import com.capstone.EventEase.UTIL.ImageUtils;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final UserEventRepository userEventRepository;



    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;


    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);




    private final ImageService imageService;

    private final UserEventService userEventService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private static final ZoneId UTC_8 = ZoneId.of("Asia/Singapore");

    private static final Logger logger = Logger.getLogger(EventService.class.getName());



    private byte[] getDefaultProfilePicture (){

        try{
            Path path = Paths.get(new ClassPathResource("static/images/lyza.jpg").getURI());
            return Files.readAllBytes(path);
        }catch (IOException e){
            System.out.println("Error: " + e.getMessage());
            return new byte[0];
        }
    }


    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }



    @Transactional(rollbackFor = {GenderNotAllowedException.class, DoubleJoinException.class, EventFullException.class, UserBlockedException.class, EntityNotFoundException.class, MessagingException.class})
    public Event createEvent(String nameUser, Event event) throws GenderNotAllowedException, DoubleJoinException, EventFullException, UserBlockedException,
            EntityNotFoundException, MessagingException {

        User userCreator = userRepository.findByUsername(nameUser);
        if (userCreator == null) {
            throw new EntityNotFoundException("User Not Found!");
        }
        event.setCreatedBy(userCreator.getFirstName());
        validateEventDatesCreated(event);


        Event newEvent = eventRepository.save(event);
        List<EmailSendRequestDTO> emails = new ArrayList<>();

        System.out.println("Event starts: " + event.getEventStarts());
        System.out.println("Event ends: " + event.getEventEnds());

        for (String username : event.getPreRegisteredUsers()) {
            if (isValidEmail(username)) {
                emails.add(createEmailDTO(username));
            } else {
                throw new MessagingException("Invalid email address: " + username);
            }
        }

        emailService.emailSend(emails, newEvent);
        logEventData(newEvent);
        return newEvent;
    }

    /*
    public Event createEvent(String nameUser, Event event) throws GenderNotAllowedException, DoubleJoinException, EventFullException, UserBlockedException,
            EntityNotFoundException {

        User userCreator = userRepository.findByUsername(nameUser);
        if (userCreator == null) {
            throw new EntityNotFoundException("User Not Found!");
        }
        event.setCreatedBy(userCreator.getFirstName());
        validateEventDates(event);

        Event newEvent = eventRepository.save(event);
        List<EmailSendRequestDTO> emails = new ArrayList<>();

        System.out.println("Event starts: " + event.getEventStarts());
        System.out.println("Event ends: " + event.getEventEnds());

        for (String username : event.getPreRegisteredUsers()) {
            // User user = createUser(username, passwords);
            emails.add(createEmailDTO(username));
            // userEventService.joinEvent(user.getId(), newEvent.getId());
        }

     //   emailService.emailSend(emails, newEvent);
        logEventData(newEvent);
        return newEvent;
    }

     */

/*
    public void sendEmails(List<String> emails, Event event) {
        List<EmailSendRequestDTO> emailSendRequestDTOS = emails.stream()
                .map(email -> EmailSendRequestDTO.builder()
                        .message("You have Been Invited To An Event From EventEase")
                        .subject("EventEase Event Invite")
                        .receiver(email)
                        .build())
                .collect(Collectors.toList());

        emailService.emailSend(emailSendRequestDTOS, event);
    }

    public void sendAllEventsEmails(){
        List<Event> events = eventRepository.findAll().stream().filter(event -> !event.isDoneSending()).toList();
        for (Event event : events) {
            Set<String> emails = event.getPreRegisteredUsers();
            sendEmails(new ArrayList<>(emails), event);
            event.setDoneSending(true);
            eventRepository.save(event);
        }
    }


 */

    private void validateEventDatesCreated(Event newEvent) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(UTC_8);
        ZonedDateTime eventStart = newEvent.getEventStarts().atZoneSameInstant(UTC_8);
        ZonedDateTime eventEnd = newEvent.getEventEnds().atZoneSameInstant(UTC_8);

        // Validate event isn't in the past
        if (eventStart.isBefore(currentDateTime)) {
            throw new DateTimeException("Event cannot start in the past.");
        }

        // Validate event end is after start
        if (eventEnd.isBefore(eventStart)) {
            throw new DateTimeException("Event cannot end before it starts.");
        }

        // Check for overlaps with existing events
        List<Event> existingEvents = eventRepository.findAll();
        for (Event existingEvent : existingEvents) {
            ZonedDateTime existingStart = existingEvent.getEventStarts().atZoneSameInstant(UTC_8);
            ZonedDateTime existingEnd = existingEvent.getEventEnds().atZoneSameInstant(UTC_8);

            // Check for overlaps using the same logic as in update
            boolean overlap = (eventStart.isBefore(existingEnd) && eventEnd.isAfter(existingStart)) ||
                    (existingStart.isBefore(eventEnd) && existingEnd.isAfter(eventStart));

            if (overlap) {
                throw new DateTimeException("Event overlaps with an existing event.");
            }
        }
    }




    private void validateEventDates(Event newEvent, Long updatingEventId) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(UTC_8);
        ZonedDateTime eventStart = newEvent.getEventStarts().atZoneSameInstant(UTC_8);
        ZonedDateTime eventEnd = newEvent.getEventEnds().atZoneSameInstant(UTC_8);

        if (eventStart.isBefore(currentDateTime)) {
            throw new DateTimeException("Event cannot start in the past.");
        }

        if (eventEnd.isBefore(eventStart)) {
            throw new DateTimeException("Event cannot end before it starts.");
        }

        List<Event> existingEvents = eventRepository.findAll();
        for (Event existingEvent : existingEvents) {
            // Skip the event being updated
            if (existingEvent.getId().equals(updatingEventId)) {
                continue;
            }

            ZonedDateTime existingStart = existingEvent.getEventStarts().atZoneSameInstant(UTC_8);
            ZonedDateTime existingEnd = existingEvent.getEventEnds().atZoneSameInstant(UTC_8);

            // Check for overlaps
            boolean overlap = (eventStart.isBefore(existingEnd) && eventEnd.isAfter(existingStart)) ||
                    (existingStart.isBefore(eventEnd) && existingEnd.isAfter(eventStart));

            if (overlap) {
                throw new DateTimeException("Event overlaps with an existing event.");
            }
        }
    }


    private User createUser(String username, List<String> passwords) {
        User findUser = userRepository.findByUsername(username);
        if (findUser != null) {
            throw new EntityExistsException("User: " + findUser.getUsername() + " Already has an account");
        }

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String userPass = passwordGenerator.generatePassword(8);
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .username(username)
                .uuid(UUID.randomUUID().toString())
                .IdNumber("99-999-99")
                .password(passwordEncoder.encode(userPass))
                .department("")
                .gender(Gender.valueOf("MALE"))
                .isBlocked(false)
                .isVerified(true)
                .role(Role.STUDENT)
                .profilePicture(ImageUtils.compressImage(getDefaultProfilePicture()))
                .profilePictureName("XyloGraph1.png")
                .profilePictureType("image/png")
                .build();
        userRepository.save(user);
        passwords.add(userPass);
        return user;
    }


    private EmailSendRequestDTO createEmailDTO(String username) {
        return EmailSendRequestDTO.builder()
                .message("You have Been Invited To An Event From EventEase")
                .subject("EventEase Event Invite")
                .receiver(username)
                .build();
    }





    public void logEventData(Event event) {
        logger.info("Event Data Picture is: " + Arrays.toString(event.getEventPicture()));
        logger.info("Event Name is: " + event.getEventName());
    }


    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event Dont Exists!"));
    }


    public String deleteEvent(Long eventId) throws EntityNotFoundException {
        Event event = getEvent(eventId);
        List<UserEvent> userEvents = userEventRepository.findByEventId(eventId);

        for (UserEvent userEvent : userEvents) {
            attendanceRepository.findByUserevent(userEvent).ifPresent(attendanceRepository::delete);
            userEventRepository.delete(userEvent);
        }
        eventRepository.deleteById(eventId);
        return "Event Has been Deleted";
    }



    public Event getByEventStarts(OffsetDateTime date){
        Event event = eventRepository.findByEventStarts(date);
        if(event == null){
            throw new EntityNotFoundException("Event with that date Not Found!");
        }
        return event;
    }


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event updateEvent(Long eventId, Event event) {
        Event oldEvent = getEvent(eventId);

        ZonedDateTime currentDate = ZonedDateTime.now(UTC_8);
        ZonedDateTime eventStart = oldEvent.getEventStarts().atZoneSameInstant(UTC_8);
        ZonedDateTime eventEnd = oldEvent.getEventEnds().atZoneSameInstant(UTC_8);

        if (event.getEventStarts() != null || event.getEventEnds() != null) {
            validateEventDates(event, eventId);
        }


        if(currentDate.isAfter(eventStart) && currentDate.isBefore(eventEnd)){
            throw new DateTimeException("Event Can't Be Updated During Event Starts");
        }
        if(currentDate.isAfter(eventEnd)){
            throw new DateTimeException("Event Can't Be Updated After Event Ends");
        }

        if (event.getEventName() != null && !event.getEventName().isEmpty()) {
            oldEvent.setEventName(event.getEventName());
        }
        if (event.getEventDescription() != null && !event.getEventDescription().isEmpty()) {
            oldEvent.setEventDescription(event.getEventDescription());
        }
        if (event.getEventStarts() != null) {
            oldEvent.setEventStarts(event.getEventStarts());
        }
        if (event.getEventEnds() != null) {
            oldEvent.setEventEnds(event.getEventEnds());
        }
        if (event.getDepartment() != null && !event.getDepartment().isEmpty()) {
            oldEvent.setDepartment(event.getDepartment());
        }
        if (event.getEventType() != null && !event.getEventType().isEmpty()) {
            oldEvent.setEventType(event.getEventType());
        }
        if (event.getEventLimit() != null && event.getEventLimit() > 0) {
            oldEvent.setEventLimit(event.getEventLimit());
        }

        if (event.getAllowedGender() != null) {
            oldEvent.setAllowedGender(event.getAllowedGender());
        }

        Event updatedEvent = eventRepository.save(oldEvent);

        List<User> usersJoined = userEventService.getAllUsersJoinedToEvent(updatedEvent.getId());
        List<EmailSendRequestDTO> emails = new ArrayList<>();
        for(User user: usersJoined){
            String username = user.getUsername();
            EmailSendRequestDTO emailSendRequestDTO = createEmailDTO(username);
            emails.add(emailSendRequestDTO);
        }
        emailService.emailUpdateSend(emails,event);
        return updatedEvent;
    }




    public OffsetDateTime getEventStarts(Long eventId){
        Event event = getEvent(eventId);
        return event.getEventStarts();
    }




    public User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User Not Found"));
    }


    public Event likeEvent(Long eventId, Long userId) {
        Event event = getEvent(eventId);
        User user = getUser(userId);

        if (hasUserLikedEvent(event, user)) {
            throw new IllegalStateException("User already liked the event");
        }

        likeEventAndUpdate(event, user);

        return event;
    }

    private boolean hasUserLikedEvent(Event event, User user) {
        return event.getUsersLiked().contains(user.getUsername());
    }

    private void likeEventAndUpdate(Event event, User user) {
        event.setLikes(event.getLikes() + 1);
        event.getUsersLiked().add(user.getUsername());

        if (event.getUsersDisliked().contains(user.getUsername())) {
            event.setDislikes(event.getDislikes() - 1);
            event.getUsersDisliked().remove(user.getUsername());
        }

        eventRepository.save(event);
    }

    public Event dislikeEvent(Long eventId, Long userId) {
        Event event = getEvent(eventId);
        User user = getUser(userId);

        if (event.getUsersDisliked().contains(user.getUsername())) {
            throw new IllegalStateException("User already disliked the event");
        }

        event.setDislikes(event.getDislikes() + 1);
        event.getUsersDisliked().add(user.getUsername());

        if (event.getUsersLiked().contains(user.getUsername())) {
            event.setLikes(event.getLikes() - 1);
            event.getUsersLiked().remove(user.getUsername());
        }

        eventRepository.save(event);
        return event;
    }




//    public Optional<Event> getEventByNow() {
//        return eventRepository.findByCurrentEvent(OffsetDateTime.now(),OffsetDateTime.now().minusMinutes(5), OffsetDateTime.now().plusMinutes(5));
//    }





    public Optional<Event> getEventByNow() {
        return eventRepository.findByCurrentEvent(OffsetDateTime.now());
        }


}

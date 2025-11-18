package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.registration.RegistrationCreateDto;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.entity.Registration;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.enums.UserRole;
import com.br.eventmanagement.exceptions.BadRequestException;
import com.br.eventmanagement.exceptions.EntityAlreadyExistsException;
import com.br.eventmanagement.repositories.RegistrationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrationService Test")
class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    private Event event;
    private User user;
    private Registration registration;
    private Registration registration2;
    private RegistrationCreateDto registrationDto;
    //TEST DELETE METHODS
    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(UUID.fromString("fa7970df-eaed-4bfc-a970-638017ee8f6a"))
                .title("Game meeting")
                .location("New York, USA")
                .date(LocalDateTime.now().plusDays(10))
                .maxParticipants(100)
                .registeredParticipants(50)
                .build();

        user = User.builder()
                .id(UUID.fromString("fa9e7123-bf40-454d-8820-6e6dce717c8d"))
                .username("allison")
                .email("allison@gmail.com")
                .password("allison1234")
                .role(UserRole.PARTICIPANT)
                .createdAt(LocalDateTime.now())
                .build();

        registration = Registration.builder()
                .id(UUID.fromString("d5c29a12-1989-4bc7-89f1-833ddb2a2f5d"))
                .user(user)
                .event(event)
                .createdAt(LocalDateTime.now())
                .build();

        registration2 = Registration.builder()
                .id(UUID.fromString("d7108c4c-9b39-4b5c-8818-de48102b5920"))
                .user(user)
                .event(event)
                .createdAt(LocalDateTime.now().plusHours(2))
                .build();

        registrationDto = RegistrationCreateDto.builder()
                .userId(user.getId())
                .eventId(event.getId())
                .build();

    }

    @Test
    @DisplayName("getById() - Should get Registration when is successful ")
    void getById_shouldReturnRegistration_WhenSuccessful(){
        when(registrationRepository.findById(registration.getId())).thenReturn(Optional.of(registration));

        Registration result = registrationService.getById(registration.getId());

        assertNotNull(result);
        assertEquals(registration, result);
        verify(registrationRepository, times(1)).findById(registration.getId());

    }

    @Test
    @DisplayName("getById() - Should throw EntityNotFoundException when Registration is not found")
    void getById_shouldThrowEntityNotFoundException_WhenRegistrationIsNotFound(){
        when(registrationRepository.findById(registration.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.getById(registration.getId())
        );

        assertEquals("Registration not found", exception.getMessage());

        verify(registrationRepository, times(1)).findById(registration.getId());

    }

    @Test
    @DisplayName("listAll() - Should return all Registrations when successful")
    void listAll_shouldReturnAllRegistrations_WhenSuccessful(){
        when(registrationRepository.findAll()).thenReturn(List.of(registration, registration2));

        List<Registration> result = registrationService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(registration, result.getFirst());
        assertEquals(registration2, result.getLast());
    }

    @Test
    @DisplayName("findAllByUserId() - Should return all Registrations by user id when successful")
    void findAllByUserId_shouldReturnAllRegistrationsByUser_WhenSuccessful(){
        when(userService.getById(user.getId())).thenReturn(user);
        when(registrationRepository.findAllByUserId(user.getId())).thenReturn(List.of(registration, registration2));

        List<Registration> result = registrationService.findAllByUserId(user.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(registration, result.getFirst());
        assertEquals(registration2, result.getLast());
    }

    @Test
    @DisplayName("findAllByUserId() - Should throw EntityNotFoundException when user is not found")
    void findAllByUserId_shouldThrowEntityNotFoundException_WhenUserNotFound(){
        when(userService.getById(user.getId())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.findAllByUserId(user.getId())
        );

        assertEquals("User not found", exception.getMessage());

        verify(userService, times(1)).getById(user.getId());
        verifyNoInteractions(registrationRepository);
    }

    @Test
    @DisplayName("findAllByEventId() - Should return all Registrations by event id when successful")
    void findAllByEventId_shouldReturnAllRegistrationsByEvent_WhenSuccessful(){
        when(eventService.getById(event.getId())).thenReturn(event);
        when(registrationRepository.findAllByEventId(event.getId())).thenReturn(List.of(registration, registration2));

        List<Registration> result = registrationService.findAllByEventId(event.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(registration, result.getFirst());
        assertEquals(registration2, result.getLast());
    }

    @Test
    @DisplayName("findAllByEventId() - Should throw EntityNotFoundException when event is not found")
    void findAllByEventId_shouldThrowEntityNotFoundException_WhenEventNotFound(){
        when(eventService.getById(event.getId())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.findAllByEventId(event.getId())
        );

        assertEquals("Event not found", exception.getMessage());

        verify(eventService, times(1)).getById(event.getId());
        verifyNoInteractions(registrationRepository);
    }

    @Test
    @DisplayName("create() - Should create a Registration successfully")
    void create_shouldCreateRegistration_WhenSuccessful(){

        when(eventService.isAvailableFreeSpot(event.getId())).thenReturn(true);
        when(registrationRepository.existsRegistrationByUserIdAndEventId(user.getId(), event.getId()))
                .thenReturn(false);
        when(eventService.getById(event.getId())).thenReturn(event);
        when(userService.getById(user.getId())).thenReturn(user);
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Integer countParticipantsBeforeUpdate = event.getRegisteredParticipants();

        Registration result = registrationService.create(registrationDto);

        assertNotNull(result);
        assertEquals(registration, result);
        assertEquals(result.getEvent().getRegisteredParticipants(), countParticipantsBeforeUpdate + 1);
        verify(eventService, times(1)).isAvailableFreeSpot(event.getId());
        verify(registrationRepository, times(1)).existsRegistrationByUserIdAndEventId(user.getId(), event.getId());
        verify(eventService, times(1)).getById(event.getId());
        verify(userService, times(1)).getById(user.getId());
        verify(registrationRepository, times(1)).save(any(Registration.class));

    }

    @Test
    @DisplayName("create() - Should throw EntityAlreadyExistsException when Registration by user and event already exists ")
    void create_shouldThrowEntityAlreadyExistsException_WhenRegistrationAlreadyExists(){

        when(registrationRepository.existsRegistrationByUserIdAndEventId(user.getId(), event.getId())).thenReturn(true);

        EntityAlreadyExistsException exception = assertThrows(
                EntityAlreadyExistsException.class,
                () -> registrationService.create(registrationDto)
        );


        assertEquals("This user is already registered", exception.getMessage());
        verify(registrationRepository, times(1)).existsRegistrationByUserIdAndEventId(user.getId(), event.getId());

        verifyNoInteractions(eventService);
        verifyNoInteractions(userService);

        //It checks if anything *else* was called in registrationRepository
        verifyNoMoreInteractions(registrationRepository);

    }

    @Test
    @DisplayName("create() - Should throw BadRequestException when there is no available free spot in the Event to create")
    void create_shouldThrowBadRequestException_WhenThereIsNoAvailableFreeSpot(){

        when(registrationRepository.existsRegistrationByUserIdAndEventId(user.getId(), event.getId())).thenReturn(false);
        when(eventService.isAvailableFreeSpot(event.getId())).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> registrationService.create(registrationDto)
        );

        assertEquals("There is no spot available in this event", exception.getMessage());

        //first 'if'
        verify(registrationRepository, times(1)).existsRegistrationByUserIdAndEventId(user.getId(), event.getId());
        verify(eventService, times(1)).isAvailableFreeSpot(event.getId());

        verifyNoInteractions(userService);

        verifyNoMoreInteractions(eventService);
        verifyNoMoreInteractions(registrationRepository);
        //it had executed one time, the first 'if', in spite of that (I used no more interactions followed after this first one)
    }

    @Test
    @DisplayName("create() - Should throw EntityNotFoundException when event not found")
    void create_shouldThrowEntityNotFoundException_WhenEventNotFound(){
        when(registrationRepository.existsRegistrationByUserIdAndEventId(user.getId(), event.getId())).thenReturn(false);
        when(eventService.isAvailableFreeSpot(event.getId())).thenReturn(true);
        /* Why I am throwing the exception?
        * Because I'm testing the RegistrationService, so it doesn't matter how EventService throw the error.
        *  I just throw it(error) */
        when(eventService.getById(event.getId())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.create(registrationDto)
        );

        assertEquals("Event not found", exception.getMessage());

        verify(registrationRepository, times(1)).existsRegistrationByUserIdAndEventId(user.getId(), event.getId());
        verify(eventService, times(1)).isAvailableFreeSpot(event.getId());
        verify(eventService, times(1)).getById(event.getId());

        verifyNoInteractions(userService);
        verifyNoMoreInteractions(registrationRepository);
    }

    @Test
    @DisplayName("create() - Should throw EntityNotFoundException when user not found")
    void create_shouldThrowEntityNotFoundException_WhenUserNotFound(){
        when(registrationRepository.existsRegistrationByUserIdAndEventId(user.getId(), event.getId())).thenReturn(false);
        when(eventService.isAvailableFreeSpot(event.getId())).thenReturn(true);
        when(eventService.getById(event.getId())).thenReturn(event);

        when(userService.getById(user.getId())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.create(registrationDto)
        );

        assertEquals("User not found", exception.getMessage());

        verify(registrationRepository, times(1)).existsRegistrationByUserIdAndEventId(user.getId(), event.getId());
        verify(eventService, times(1)).isAvailableFreeSpot(event.getId());
        verify(eventService, times(1)).getById(event.getId());
        verify(userService, times(1)).getById(user.getId());

        verifyNoMoreInteractions(registrationRepository);


    }

    @Test
    @DisplayName("deleteByUserIdAndEventId() - Should delete a Registration when successful")
    void deleteByUserIdAndEvent_ShouldDeleteRegistration_WhenSuccessful(){
        when(registrationRepository.findByUserIdAndEventId(user.getId(), event.getId())).thenReturn(Optional.of(registration));

        /*
        * It literally means "execute zero lines of code."
        * Think of doNothing() as hollowing out the method. It replaces the method's entire body with an empty block { }.
        * You verify the interaction (the method call), not the state change
        * (the data deletion, this data verification would be in repository and integration tests).
        * */
        doNothing().when(registrationRepository).delete(registration);

        registrationService.deleteByUserIdAndEventId(user.getId(),event.getId());

        verify(registrationRepository, times(1)).findByUserIdAndEventId(user.getId(), event.getId());
        verify(registrationRepository, times(1)).delete(registration);
    }

    @Test
    @DisplayName("deleteByUserIdAndEventId() -  Should throw EntityNotFoundException when Registration not found")
    void deleteByUserIdAndEventId_ShouldThrowEntityNotFoundException_WhenRegistrationNotFound(){
        when(registrationRepository.findByUserIdAndEventId(user.getId(), event.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            registrationService.deleteByUserIdAndEventId(user.getId(), event.getId());
        });

        assertEquals("There is no registration of this user to this event", exception.getMessage());
        verify(registrationRepository, times(1)).findByUserIdAndEventId(user.getId(), event.getId());
        verifyNoMoreInteractions(registrationRepository);
    }

    @Test
    @DisplayName("deleteAllByUserId() - Should delete all Registrations by user id when successful")
    void deleteAllByUserId_ShouldDeleteAllRegistrationsByUserId_WhenSuccessful(){
        when(registrationService.findAllByUserId(user.getId())).thenReturn(List.of(registration, registration2));

        doNothing().when(registrationRepository).delete(any(Registration.class));

        registrationService.deleteAllByUserId(user.getId());

        verify(registrationRepository, times(1)).findAllByUserId(user.getId());
        verify(registrationRepository, times(2)).delete(any(Registration.class));

    }

    @Test
    @DisplayName("deleteAllByUserId() - Should throw EntityNotFoundException when user is not found")
    void deleteAllByUserId_ShouldThrowEntityNotFoundExceptionWhenUserIsNoFound(){
        when(registrationService.findAllByUserId(user.getId())).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.deleteAllByUserId(user.getId())
        );

        assertEquals("User not found", exception.getMessage());
        verify(registrationRepository, times(1)).findAllByUserId(user.getId());
        verifyNoMoreInteractions(registrationRepository);
    }

    @Test
    @DisplayName("deleteAllByEventId() - Should delete a all Registrations by event id when successful")
    void deleteAllByEventId_ShouldDeleteAllRegistrationsByEventId_WhenSuccessful(){
        when(eventService.getById(event.getId())).thenReturn(event);

        doNothing().when(registrationRepository).deleteAllByEventId(event.getId());

        registrationService.deleteAllByEventId(event.getId());

        verify(eventService, times(1)).getById(event.getId());
        verify(registrationRepository, times(1)).deleteAllByEventId(event.getId());
        assertEquals(0, event.getRegisteredParticipants()); //checking if the participants were updated

    }

    @Test
    @DisplayName("deleteAllByEventId() - Should throw EntityNotFoundException when event is not found")
    void deleteAllByEventId_ShouldThrowEntityNotFoundExceptionWhenEventIsNotFound(){
        when(eventService.getById(event.getId())).thenThrow(new EntityNotFoundException("Event not found"));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.deleteAllByEventId(event.getId())
        );

        assertEquals("Event not found", exception.getMessage());
        verify(eventService, times(1)).getById(event.getId());
        verifyNoInteractions(registrationRepository);

    }

    @Test
    @DisplayName("deleteById() - Should delete by Registration id when successful")
    void deleteById_ShouldDeleteByRegistrationIdWhenSuccessful(){
        // Don't mock the service. Mock the dependency that the service uses.
        // This makes 'this.getById()' succeed internally.
        //The RegistrationService is not a mock but is a real object, so I have to mock his repository
        when(registrationRepository.findById(registration.getId()))
                .thenReturn(Optional.of(registration));

        doNothing().when(registrationRepository).delete(registration);

        registrationService.deleteById(registration.getId());

        verify(registrationRepository, times(1)).findById(registration.getId());
        verify(registrationRepository, times(1)).delete(registration);
    }
    @Test
    @DisplayName("deleteById() - Should throw EntityNotFoundException when registration is not found")
    void deleteById_ShouldThrowEntityNotFoundExceptionWhenRegistrationIsNotFound(){
        when(registrationRepository.findById(registration.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> registrationService.deleteById(registration.getId())
        );

        assertEquals("Registration not found", exception.getMessage());
        verify(registrationRepository, times(1)).findById(registration.getId());
        verifyNoMoreInteractions(registrationRepository);
    }
}
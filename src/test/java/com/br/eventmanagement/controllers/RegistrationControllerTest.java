package com.br.eventmanagement.controllers;

import com.br.eventmanagement.dtos.registration.RegistrationCreateDto;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.entity.Registration;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.enums.UserRole;
import com.br.eventmanagement.exceptions.BadRequestException;
import com.br.eventmanagement.exceptions.EntityAlreadyExistsException;
import com.br.eventmanagement.security.TokenService;
import com.br.eventmanagement.services.RegistrationService;
import com.br.eventmanagement.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    //these two mocks are just to avoid security problems
    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    private Event event;
    private User user;
    private Registration registration;
    private Registration registration2;
    private RegistrationCreateDto registrationDto;

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
    @DisplayName("getById() - should getById a registration when successful")
    void getById_shouldGetByIdRegistrationWhenSuccessful() throws Exception{
        String expectedJson = objectMapper.writeValueAsString(registration);

        when(registrationService.getById(registration.getId())).thenReturn(registration);

        mockMvc.perform(get("/registration/{id}", registration.getId() ))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("getById() - should return 404 when a registration is not found")
    void getById_shouldReturn404WhenRegistrationIsNotFound() throws Exception{
        doThrow(new EntityNotFoundException("Registration not found"))
                .when(registrationService).getById(registration.getId());

        mockMvc.perform(get("/registration/{id}", registration.getId() ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("listAll() - should list all registrations when successful")
    void listAll_shouldListAllRegistrationsWhenSuccessful() throws Exception{
        List<Registration> output = List.of(registration, registration2);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(registrationService.listAll()).thenReturn(output);

        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("findAllByUserId() - should find all registrations by user id when successful")
    void findAllByUserId_shouldFindAllRegistrationByUserIdWhenSuccessful() throws Exception{
        List<Registration> output = List.of(registration, registration2);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(registrationService.findAllByUserId(user.getId())).thenReturn(output);

        mockMvc.perform(get("/registration/user/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("findAllByEventId() - should find all registrations by event id when successful")
    void findAllByEventId_shouldFindAllRegistrationsByEventIdWhenSuccessful() throws Exception{
        List<Registration> output = List.of(registration, registration2);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(registrationService.findAllByEventId(event.getId())).thenReturn(output);

        mockMvc.perform(get("/registration/event/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


    @Test
    @DisplayName("create() - should create a registration when successful")
    void create_shouldCreateRegistrationWhenSuccessful() throws Exception {
        when(registrationService.create(any(RegistrationCreateDto.class))).thenReturn(registration);

        mockMvc.perform(
                        post("/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(registration.getId().toString()))

                .andExpect(jsonPath("$.user.id").value(registration.getUser().getId().toString()))
                .andExpect(jsonPath("$.user.username").value(registration.getUser().getUsername()))
                .andExpect(jsonPath("$.user.email").value(registration.getUser().getEmail()))

                .andExpect(jsonPath("$.event.id").value(registration.getEvent().getId().toString()))
                .andExpect(jsonPath("$.event.title").value(registration.getEvent().getTitle()))
                .andExpect(jsonPath("$.event.location").value(registration.getEvent().getLocation()))
                .andExpect(jsonPath("$.event.maxParticipants").value(registration.getEvent()
                        .getMaxParticipants().toString()))
                .andExpect(jsonPath("$.event.registeredParticipants").value(registration.getEvent()
                        .getRegisteredParticipants().toString()))


                .andExpect(jsonPath("$.createdAt").value(registration.getCreatedAt().toString()));

    }

    @Test
    @DisplayName("create() - should return 409 when a registration already exists")
    void create_shouldReturn409WhenRegistrationAlreadyExists() throws Exception {
        doThrow(new EntityAlreadyExistsException("Registration already exists"))
                .when(registrationService).create(registrationDto);

        mockMvc.perform(
                post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto))
        ).andExpect(status().isConflict());

    }

    @Test
    @DisplayName("create() - should return 400 when there is no spot available for registration")
    void create_shouldReturn400WhenThereIsNoSpotAvailable() throws Exception {
        doThrow(new BadRequestException("There is no spot available"))
                .when(registrationService).create(registrationDto);

        mockMvc.perform(
                post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto))
        ).andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("deleteById() - should delete registration by id when successful")
    void deleteById_shouldDeleteRegistrationByIdWhenSuccessful() throws Exception{
        doNothing().when(registrationService).deleteById(registration.getId());

        mockMvc.perform(delete("/registration/{id}", registration.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deleteByUserIdAndEventId() - should delete registration by user id and event id when successful")
    void deleteByUserIdAndEventId_shouldDeleteRegistrationByUserIdAndEventIdWhenSuccessful() throws Exception{
        doNothing().when(registrationService)
                .deleteByUserIdAndEventId(registration.getUser().getId(), registration.getEvent().getId());

        mockMvc.perform(delete("/registration/user/{idUser}/event/{idEvent}"
                        , registration.getUser().getId(), registration.getEvent().getId()))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("deleteByUserIdAndEventId() - should return 404 when registration by userId and eventId is not found")
    void deleteByUserIdAndEventId_shouldReturn404WhenRegistrationByUserIdAndEventIdIsNotFound() throws Exception{
        doThrow(new EntityNotFoundException("Registration not found")).when(registrationService)
                .deleteByUserIdAndEventId(registration.getUser().getId(), registration.getEvent().getId());

        mockMvc.perform(
                delete("/registration/user/{idUser}/event/{idEvent}"
                        , registration.getUser().getId(), registration.getEvent().getId()))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("deleteAllByUserId() - should delete all registrations by user id when successful")
    void deleteAllByUserId_shouldDeleteAllRegistrationsByUserIdWhenSuccessful() throws Exception{
        doNothing().when(registrationService)
                .deleteAllByUserId(registration.getUser().getId());

        mockMvc.perform(delete("/registration/user/{id}", registration.getUser().getId()))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("deleteAllByEventId() - should delete all registrations by event id when successful")
    void deleteAllByEventId_shouldDeleteAllRegistrationsByEventIdWhenSuccessful() throws Exception{
        doNothing().when(registrationService)
                .deleteAllByEventId(registration.getEvent().getId());

        mockMvc.perform(delete("/registration/event/{id}", registration.getEvent().getId()))
                .andExpect(status().isNoContent());

    }
}
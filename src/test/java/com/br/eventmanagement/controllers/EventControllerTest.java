package com.br.eventmanagement.controllers;

import com.br.eventmanagement.dtos.event.*;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.security.TokenService;
import com.br.eventmanagement.services.EventService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;

    private Event event;
    private Event event2;

    @BeforeEach
    void setUp(){
        event = Event.builder()
                .id(UUID.fromString("fa7970df-eaed-4bfc-a970-638017ee8f6a"))
                .title("Game meeting")
                .location("New York, USA")
                .date(LocalDateTime.now().plusDays(10))
                .maxParticipants(100)
                .registeredParticipants(50)
                .build();
        event2 = Event.builder()
                .id(UUID.fromString("a2dc3f60-0ba9-44c8-9909-981f703a69f1"))
                .title("Coffee Party")
                .location("Sao Paulo, Brazil")
                .date(LocalDateTime.now().plusDays(10))
                .maxParticipants(20)
                .registeredParticipants(5)
                .build();

    }

    @Test
    @DisplayName("listAll() - should list all events when successful")
    void listAll_shouldListAllEventsWhenSuccessful() throws Exception {
        List<Event> output = List.of(event, event2);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(eventService.listAll()).thenReturn(output);

        mockMvc.perform(get("/event"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("getById() - should get event by id when successful")
    void getById_shouldGetEventByIdWhenSuccessful() throws Exception {
        String expectedJson = objectMapper.writeValueAsString(event);

        when(eventService.getById(event.getId())).thenReturn(event);

        mockMvc.perform(get("/event/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("getById() - should return 404 when event by id is not found")
    void getById_shouldReturn404WhenEventByIdIsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Event not found")).when(eventService).getById(event.getId());

        mockMvc.perform(get("/event/{id}", event.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("findAllByDate() - should find all events by date when successful")
    void findAllByDate_shouldFindAllEventsByDateWhenSuccessful() throws Exception{
        List<Event> output = List.of(event, event2);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(eventService.findAllByDate(event.getDate().toLocalDate())).thenReturn(output);

        mockMvc.perform(get("/event/date/{date}", event.getDate().toLocalDate()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("isAvailableFreeSpot() - should verify if the spot is available when successful")
    void isAvailableFreeSpot_shouldVerifyIfTheSpotIsAvailableWhenSuccessful() throws Exception {
        when(eventService.isAvailableFreeSpot(event.getId())).thenReturn(true);

        mockMvc.perform(get("/event/isAvailableFreeSpot/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("listAllAvailable() - should list all available events when successful ")
    void listAllAvailable_shouldListAllAvailableEventsWhenSuccessful() throws Exception {
        List<Event> output = List.of(event, event2);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(eventService.listAllAvailable()).thenReturn(output);

        mockMvc.perform(get("/event/available"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("searchEvents() - should search events by params title and location when successful")
    void searchEvents_shouldSearchEventsByParamsTitleAndLocationWhenSuccessful() throws Exception  {
        List<Event> output = List.of(event);
        String expectedJson = objectMapper.writeValueAsString(output);

        when(eventService.searchEvents(event.getTitle(), event.getLocation())).thenReturn(output);

        mockMvc.perform(get("/event/filter")
                .param("title", event.getTitle())
                .param("location", event.getLocation()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("create() - should create a event when successful")
    void create_shouldCreateEventWhenSuccessful() throws Exception {
        EventCreateDto eventCreateDto = EventCreateDto.builder()
                .title(event.getTitle())
                .location(event.getLocation())
                .date(event.getDate())
                .maxParticipants(event.getMaxParticipants())
                .build();
        String expectedJson = objectMapper.writeValueAsString(event);

        when(eventService.create(any(EventCreateDto.class))).thenReturn(event);

        mockMvc.perform(
                post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("updateTitle() - should update title when successful")
    void updateTitle_shouldUpdateTitleWhenSuccessful() throws Exception  {
        EventUpdateTitleDto eventUpdateTitleDto = new EventUpdateTitleDto("new title");

        doNothing().when(eventService).updateTitle(any(UUID.class), any(EventUpdateTitleDto.class));

        mockMvc.perform(patch("/event/title/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateTitleDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateTitle() - should return 404 when event to update title is not found")
    void updateTitle_shouldReturn404WhenEventIsNotFound() throws Exception  {
        EventUpdateTitleDto eventUpdateTitleDto = new EventUpdateTitleDto("new title");

        doThrow(new EntityNotFoundException("Event not found"))
                .when(eventService).updateTitle(any(UUID.class), any(EventUpdateTitleDto.class));

        mockMvc.perform(patch("/event/title/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateTitleDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("updateLocation() - should update location when successful")
    void updateLocation_shouldUpdateLocationWhenSuccessful() throws Exception {
        EventUpdateLocationDto eventUpdateLocationDto = new EventUpdateLocationDto("new location");

        doNothing().when(eventService).updateLocation(any(UUID.class), any(EventUpdateLocationDto.class));

        mockMvc.perform(patch("/event/location/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateLocationDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateLocation() - should return 404 when event to update location is not found")
    void updateLocation_shouldReturn404WhenEventIsNotFound() throws Exception  {
        EventUpdateLocationDto eventUpdateLocationDto = new EventUpdateLocationDto("new location");

        doThrow(new EntityNotFoundException("Event not found"))
                .when(eventService).updateLocation(any(UUID.class), any(EventUpdateLocationDto.class));

        mockMvc.perform(patch("/event/location/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateLocationDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("updateDate() - should update date when successful")
    void updateDate_shouldUpdateDateWhenSuccessful() throws Exception {
        EventUpdateDateDto eventUpdateDateDto = new EventUpdateDateDto(LocalDateTime.now().plusDays(2));

        doNothing().when(eventService).updateDate(any(UUID.class), any(EventUpdateDateDto.class));

        mockMvc.perform(patch("/event/date/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateDateDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateDate() - should return 404 when event to update date is not found")
    void updateDate_shouldReturn404WhenEventIsNotFound() throws Exception  {
        EventUpdateDateDto eventUpdateDateDto = new EventUpdateDateDto(LocalDateTime.now().plusDays(2));

        doThrow(new EntityNotFoundException("Event not found"))
                .when(eventService).updateDate(any(UUID.class), any(EventUpdateDateDto.class));

        mockMvc.perform(patch("/event/date/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateDateDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("updateParticipants() - should update participants when successful")
    void updateParticipants_shouldUpdateParticipantsWhenSuccessful() throws Exception {
        EventUpdateParticipantsDto eventUpdateParticipantsDto = new EventUpdateParticipantsDto(200);

        doNothing().when(eventService).updateParticipants(any(UUID.class), any(EventUpdateParticipantsDto.class));

        mockMvc.perform(patch("/event/participants/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateParticipantsDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateParticipants() - should return 404 when event to update participants is not found")
    void updateParticipants_shouldReturn404WhenEventIsNotFound() throws Exception  {
        EventUpdateParticipantsDto eventUpdateParticipantsDto = new EventUpdateParticipantsDto(200);

        doThrow(new EntityNotFoundException("Event not found"))
                .when(eventService).updateParticipants(any(UUID.class), any(EventUpdateParticipantsDto.class));

        mockMvc.perform(patch("/event/participants/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventUpdateParticipantsDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete() - should delete event when successful")
    void delete_shouldDeleteEventWhenSuccessful() throws Exception{
        doNothing().when(eventService).delete(any(UUID.class));

        mockMvc.perform(delete("/event/{id}", event.getId()))
                .andExpect(status().isNoContent());
    }
}
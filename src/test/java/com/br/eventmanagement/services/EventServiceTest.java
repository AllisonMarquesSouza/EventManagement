package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.event.*;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.repositories.EventRepository;
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
@DisplayName("EventService Unit Tests")
class EventServiceTest {
    //1 we get all injects from the class we are going to test
    //What is mock a class?
    /*
    * The purpose is to isolate the class you are testing. Why, simple imagine you are testing a method that uses the
    * repository method, you can manipulate the response of this (repository method), just to test the main that you want
    * Basically the mock never touch the database, it simulates the action results
    * */

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

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
    @DisplayName("listAll() - Should return all events when successful")
    void listAll_shouldReturnAllEventsWhenSuccessful(){
        when(eventRepository.findAll()).thenReturn(List.of(event, event2));

        List<Event> result = eventService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(event, result.getFirst());
        assertEquals(event2, result.getLast());
    }

    @Test
    @DisplayName("getById() - Should return event by id when successful")
    void getById_shouldReturnEventByIdWhenSuccessful(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        Event result = eventService.getById(event.getId());

        assertNotNull(result);
        assertEquals(event, result);
        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    @DisplayName("getById() - Should throw EntityNotFoundException when event not found")
    void getById_shouldThrowEntityNotFoundExceptionWhenEventNotFound(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> eventService.getById(event.getId())
        );

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    @DisplayName("findAllByDate() - Should return all events by date when successful")
    void findAllByDate_shouldReturnAllByDateWhenSuccessful(){
        when(eventRepository.findAllByDate(event.getDate().toLocalDate())).thenReturn(List.of(event, event2));

        List<Event> result = eventService.findAllByDate(event.getDate().toLocalDate());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(event, result.getFirst());
        assertEquals(event2, result.getLast());
    }

    @Test
    @DisplayName("listAllAvailable() - Should return all events available when spots are available")
    void listAllAvailable_shouldReturnAllEventsAvailableWhenSpotsAreAvailable(){
        when(eventRepository.findEventsWithAvailability()).thenReturn(List.of(event, event2));

        List<Event> result = eventService.listAllAvailable();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(event, result.getFirst());
        assertEquals(event2, result.getLast());
    }

    @Test
    @DisplayName("isAvailableFreeSpot() - Should return true when spot is available")
    void isAvailableFreeSpot_shouldReturnTrueWhenSpotIsAvailable(){
        when(eventRepository.isAvailableFreeSpot(event.getId())).thenReturn(true);

        boolean result = eventService.isAvailableFreeSpot(event.getId());

        assertTrue(result);
        verify(eventRepository, times(1)).isAvailableFreeSpot(event.getId());
    }

    @Test
    @DisplayName("isAvailableFreeSpot() - Should return false when no spot is available")
    void isAvailableFreeSpot_shouldReturnFalseWhenNoSpotAvailable(){
        when(eventRepository.isAvailableFreeSpot(event.getId())).thenReturn(false);

        boolean result = eventService.isAvailableFreeSpot(event.getId());

        assertFalse(result);
        verify(eventRepository, times(1)).isAvailableFreeSpot(event.getId());
    }

    @Test
    @DisplayName("searchEvents() - Should return events when searching by title")
    void searchEvents_shouldReturnEventsWhenSearchingByTitle(){
        when(eventRepository.searchEvents(event.getTitle(), null)).thenReturn(List.of(event));

        List<Event> result = eventService.searchEvents(event.getTitle(), null);

        assertFalse(result.isEmpty());
        assertEquals(event, result.getFirst());
        verify(eventRepository, times(1)).searchEvents(event.getTitle(), null);
    }

    @Test
    @DisplayName("searchEvents() - Should return events when searching by location")
    void searchEvents_shouldReturnEventsWhenSearchingByLocation(){
        when(eventRepository.searchEvents(null, event.getLocation())).thenReturn(List.of(event));

        List<Event> result = eventService.searchEvents(null, event.getLocation());

        assertFalse(result.isEmpty());
        assertEquals(event, result.getFirst());
        verify(eventRepository, times(1)).searchEvents(null, event.getLocation());
    }

    @Test
    @DisplayName("searchEvents() - Should return events when searching by title and location")
    void searchEvents_shouldReturnEventsWhenSearchingByTitleAndLocation(){
        when(eventRepository.searchEvents(event.getTitle(), event.getLocation())).thenReturn(List.of(event));

        List<Event> result = eventService.searchEvents(event.getTitle(), event.getLocation());

        assertFalse(result.isEmpty());
        assertEquals(event, result.getFirst());
        verify(eventRepository, times(1)).searchEvents(event.getTitle(), event.getLocation());
    }

    @Test
    @DisplayName("create() - Should create an Event when successful")
    void create_shouldCreatAnEventWhenSuccessful(){
        EventCreateDto eventCreateDto = EventCreateDto.builder()
                .title(event.getTitle())
                .location(event.getLocation())
                .date(event.getDate())
                .maxParticipants(event.getMaxParticipants())
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.create(eventCreateDto);

        assertNotNull(result);
        assertEquals(event, result);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("updateTitle() - Should update title successfully when event exists")
    void updateTitle_ShouldUpdateTitle_WhenEventExists(){
        UUID eventId = UUID.randomUUID();
        String oldTitle = "Old Title";
        String newTitle = "New Awesome Title";

        Event eventLocal = Event.builder()
                .id(eventId)
                .title(oldTitle)
                .build();

        EventUpdateTitleDto updateDto = new EventUpdateTitleDto(newTitle);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventLocal));

        eventService.updateTitle(eventId, updateDto);

        // Checking State, if the title was changed correctly
        assertEquals(newTitle, eventLocal.getTitle());

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(eventLocal);

    }

    @Test
    @DisplayName("updateTitle() - Should throw EntityNotFoundException when updating title for non-existent event")
    void updateTitle_ShouldThrowEntityNotFoundException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        EventUpdateTitleDto updateDto = new EventUpdateTitleDto("New Title");

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateTitle(eventId, updateDto);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any());    //verifying that save was never called
    }

    @Test
    @DisplayName("updateLocation() - Should update location successfully when event exists")
    void updateLocation_ShouldUpdateLocation_WhenEventExists(){
        UUID eventId = UUID.randomUUID();
        String oldLocation = "Old Title";
        String newLocation = "New Awesome Title";

        Event eventLocal = Event.builder()
                .id(eventId)
                .location(oldLocation)
                .build();

        EventUpdateLocationDto updateDto = new EventUpdateLocationDto(newLocation);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventLocal));

        eventService.updateLocation(eventId, updateDto);

        assertEquals(newLocation, eventLocal.getLocation());

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(eventLocal);

    }

    @Test
    @DisplayName("updateLocation() - Should throw EntityNotFoundException when updating Location for non-existent event")
    void updateLocation_ShouldThrowEntityNotFoundException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        EventUpdateLocationDto updateDto = new EventUpdateLocationDto("New Location");

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateLocation(eventId, updateDto);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateDate() - Should update date successfully when event exists")
    void updateDate_ShouldUpdateDate_WhenEventExists(){
        UUID eventId = UUID.randomUUID();
        LocalDateTime oldDate = LocalDateTime.of(2025, 12, 10, 12, 30);
        LocalDateTime newDate = LocalDateTime.of(2026, 1, 25, 16, 45);

        Event event = Event.builder()
                .id(eventId)
                .date(oldDate)
                .build();

        EventUpdateDateDto updateDateDto = new EventUpdateDateDto(newDate);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.updateDate(eventId, updateDateDto);

        assertEquals(newDate, event.getDate());

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(event);

    }

    @Test
    @DisplayName("updateDate() - Should throw EntityNotFoundException when updating Date for non-existent event")
    void updateDate_ShouldThrowEntityNotFoundException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        EventUpdateDateDto updateDto = new EventUpdateDateDto(LocalDateTime.now());

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateDate(eventId, updateDto);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateParticipants() - Should update participants successfully when event exists")
    void updateParticipants_ShouldUpdateParticipants_WhenEventExists(){
        UUID eventId = UUID.randomUUID();
        Integer oldParticipants = 50;
        Integer newParticipants = 100;

        Event event = Event.builder()
                .id(eventId)
                .maxParticipants(oldParticipants)
                .build();

        EventUpdateParticipantsDto updateParticipantsDto = new EventUpdateParticipantsDto(newParticipants);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.updateParticipants(eventId, updateParticipantsDto);

        assertEquals(newParticipants, event.getMaxParticipants());

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("updateParticipants() - Should throw EntityNotFoundException when updating participants for non-existent event")
    void updateParticipants_ShouldThrowEntityNotFoundException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        EventUpdateParticipantsDto updateDto = new EventUpdateParticipantsDto(100);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateParticipants(eventId, updateDto);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() - Should delete by id when event exists")
    void delete_shouldDeleteByIdWhenEventExists(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        doNothing().when(eventRepository).deleteById(event.getId());

        eventService.delete(event.getId());

        verify(eventRepository, times(1)).findById(event.getId());
        verify(eventRepository, times(1)).deleteById(event.getId());
    }

    @Test
    @DisplayName("delete() - Should throw EntityNotFoundException when event is not found")
    void delete_shouldThrowEntityNotFoundExceptionWhenEventIsNotFound(){
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> eventService.delete(event.getId())
        );

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(event.getId());
        verifyNoMoreInteractions(eventRepository);
    }

}
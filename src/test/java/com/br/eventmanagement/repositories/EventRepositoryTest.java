package com.br.eventmanagement.repositories;

import com.br.eventmanagement.entity.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    private Event eventBeachParty;
    private Event eventBirthDay;
    private Event eventMeeting;

    @BeforeEach
    void setUp(){
        eventBeachParty = Event.builder()
                .title("Beach party")
                .location("Rio de Janeiro, copacabana")
                .date(LocalDateTime.of(2026, 5, 10, 21, 0))
                .maxParticipants(500)
                .registeredParticipants(323)
                .build();
        eventBirthDay = Event.builder()
                .title("BirthDay")
                .location("Sao Paulo, Brazil")
                .date(LocalDateTime.of(2026, 8, 23, 18, 0))
                .maxParticipants(50)
                .registeredParticipants(15)
                .build();
        eventMeeting =  Event.builder()
                .title("Meeting")
                .location("Recife, Pernambuco")
                .date(LocalDateTime.of(2026, 10, 15, 10, 0))
                .maxParticipants(15)
                .registeredParticipants(15)
                .build();

    }

    @AfterEach
    void tearDown(){
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("findEventsWithAvailability() - should find all events with availability when successful")
    void findEventsWithAvailability_shouldFindAllEventsWithAvailabilityWhenSuccessful(){
        eventRepository.save(eventBeachParty);
        eventRepository.save(eventBirthDay);
        eventRepository.save(eventMeeting);

        List<Event> result = eventRepository.findEventsWithAvailability();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.getFirst(), eventBeachParty);
        assertEquals(result.getLast(), eventBirthDay);

    }

    @Test
    @DisplayName("isAvailableFreeSpot() - should return true when the spot is available")
    void isAvailableFreeSpot_shouldReturnTrueWhenTheSpotIsAvailable(){
        eventRepository.save(eventBeachParty);

        boolean result = eventRepository.isAvailableFreeSpot(eventBeachParty.getId());

        assertTrue(result);
    }

    @Test
    @DisplayName("isAvailableFreeSpot() - should return false when the spot is not available")
    void isAvailableFreeSpot_shouldReturnFalseWhenTheSpotIsNotAvailable(){
        eventRepository.save(eventMeeting);

        boolean result = eventRepository.isAvailableFreeSpot(eventMeeting.getId());

        assertFalse(result);
    }


    @Test
    @DisplayName("searchEvents() - should search events by title and location when successful")
    void searchEvents_shouldResearchEventsWhenSuccessful(){
        eventRepository.save(eventBeachParty);
        eventRepository.save(eventBirthDay);
        eventRepository.save(eventMeeting);

        List<Event> result = eventRepository.searchEvents("BirthDay", "Sao Paulo");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.getFirst(), eventBirthDay);
    }

}
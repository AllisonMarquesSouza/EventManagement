package com.br.eventmanagement.repositories;

import com.br.eventmanagement.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT e FROM Event e WHERE DATE(e.date) = :date")
    List<Event> findByDate(LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.registeredParticipants < e.maxParticipants")
    List<Event> findEventsWithAvailability();


    @Query("""
    SELECT e FROM Event e
        WHERE (:title IS NULL OR e.title LIKE %:title%)
        AND (:location IS NULL OR e.location LIKE %:location%)
    """)
    List<Event> filterEventByTitleAndLocation(@Param("title") String title,
                                              @Param("location") String location);
}
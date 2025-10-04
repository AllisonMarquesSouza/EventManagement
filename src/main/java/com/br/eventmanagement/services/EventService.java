package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.event.*;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.repositories.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> listAll(){
        return eventRepository.findAll();
    }

    public Event getById(UUID uuid){
        return eventRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public List<Event> getByDate(LocalDate date){
        return eventRepository.findByDate(date);
    }

    public List<Event> getAvailable(){
        return eventRepository.findEventsWithAvailability();
    }

    public List<Event> filterByTitleAndLocationAndDate(String title, String location){
        return eventRepository.filterEventByTitleAndLocation(title, location);
    }

    @Transactional
    public Event create(EventCreateDto eventCreateDto){
        return eventRepository.save(new Event(eventCreateDto));
    }

    @Transactional
    public void updateTitle(UUID id, EventUpdateTitleDto updateTitleDto){
        Event eventToUpdate = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventToUpdate.setTitle(updateTitleDto.title());
        eventRepository.save(eventToUpdate);
    }
    
    @Transactional
    public void updateLocation(UUID id, EventUpdateLocationDto updateLocationDto){
        Event eventToUpdate = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventToUpdate.setLocation(updateLocationDto.location());
        eventRepository.save(eventToUpdate);
    }
    
    @Transactional
    public void updateDate(UUID id, EventUpdateDateDto updateDateDto){
        Event eventToUpdate = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventToUpdate.setDate(updateDateDto.date());
        eventRepository.save(eventToUpdate);
    }

    @Transactional
    public void updateParticipants(UUID id, EventUpdateParticipantsDto updateParticipantsDto){
        Event eventToUpdate = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventToUpdate.setMaxParticipants(updateParticipantsDto.maxParticipants());
        eventRepository.save(eventToUpdate);
    }

    @Transactional
    public void delete(UUID id){
        eventRepository.delete(this.getById(id));
    }

}

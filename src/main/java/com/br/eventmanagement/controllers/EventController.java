package com.br.eventmanagement.controllers;

import com.br.eventmanagement.dtos.event.*;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> listAll(){
        return ResponseEntity.ok(eventService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") UUID id){
        return ResponseEntity.ok(eventService.getById(id));
    }
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Event>> getByDate(@PathVariable("date") LocalDate date){
        return ResponseEntity.ok(eventService.getByDate(date));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Event>> getAvailable(){
        return ResponseEntity.ok(eventService.getAvailable());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Event>> filterByTitleAndLocationAndDate(@RequestParam(required = false) String title,
                                                                       @RequestParam(required = false) String location
                                                                       ){
        return ResponseEntity.ok(eventService.filterByTitleAndLocationAndDate(title, location));
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody @Valid EventCreateDto createDto){
        return new ResponseEntity<>(eventService.create(createDto), HttpStatus.CREATED);
    }

    @PatchMapping("/title/{id}")
    public ResponseEntity<Void> updateTitle(@PathVariable("id") UUID id, @RequestBody @Valid EventUpdateTitleDto updateTitleDto){
        eventService.updateTitle(id, updateTitleDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/location/{id}")
    public ResponseEntity<Void> updateLocation(@PathVariable("id") UUID id, @RequestBody @Valid EventUpdateLocationDto updateLocationDto){
        eventService.updateLocation(id, updateLocationDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/date/{id}")
    public ResponseEntity<Void> updateDate(@PathVariable("id") UUID id, @RequestBody @Valid EventUpdateDateDto updateDateDto){
        eventService.updateDate(id, updateDateDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/participants/{id}")
    public ResponseEntity<Void> updateParticipants(@PathVariable("id") UUID id,
                                           @RequestBody @Valid EventUpdateParticipantsDto updateParticipantsDto){
        eventService.updateParticipants(id, updateParticipantsDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

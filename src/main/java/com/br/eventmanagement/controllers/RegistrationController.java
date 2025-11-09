package com.br.eventmanagement.controllers;

import com.br.eventmanagement.dtos.registration.RegistrationCreateDto;
import com.br.eventmanagement.entity.Registration;
import com.br.eventmanagement.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @GetMapping("/{id}") //registration id, not user or event id
    public ResponseEntity<Registration> getById(@PathVariable("id")UUID id){
        return ResponseEntity.ok(registrationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Registration>> listAll(){
        return ResponseEntity.ok(registrationService.listAll());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Registration>> findAllByUserId(@PathVariable("id") UUID id){
        return ResponseEntity.ok(registrationService.findAllByUserId(id));
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<List<Registration>> findAllByEventId(@PathVariable("id") UUID id){
        return ResponseEntity.ok(registrationService.findAllByEventId(id));
    }

    @PostMapping
    public ResponseEntity<Registration> create(@RequestBody @Valid RegistrationCreateDto createDto){
        return new ResponseEntity<>(registrationService.create(createDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id){
        registrationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/event/{eventId}")
    public ResponseEntity<Void> deleteByUserIdAndEventId(@PathVariable("userId") UUID userId,
                                                         @PathVariable("eventId") UUID eventId){
        registrationService.deleteByUserIdAndEventId(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllByUserId(@PathVariable("userId") UUID userId){
        registrationService.deleteAllByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<Void> deleteAllByEventId(@PathVariable("eventId") UUID eventId){
        registrationService.deleteAllByEventId(eventId);
        return ResponseEntity.noContent().build();
    }
}

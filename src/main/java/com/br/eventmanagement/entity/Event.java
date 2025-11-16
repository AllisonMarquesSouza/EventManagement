package com.br.eventmanagement.entity;

import com.br.eventmanagement.dtos.event.EventCreateDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "registered_participants")
    private Integer registeredParticipants;

    public Event(EventCreateDto eventCreateDto){
        this.title = eventCreateDto.title();
        this.location = eventCreateDto.location();
        this.date = eventCreateDto.date();
        this.maxParticipants = eventCreateDto.maxParticipants();
    }
}

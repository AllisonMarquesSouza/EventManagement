package com.br.eventmanagement.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventCreateDto( @NotBlank String title,
                             @NotBlank String location,
                             @NotNull @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd HH:mm")LocalDateTime date,
                             @NotNull Integer maxParticipants) {
}

package com.br.eventmanagement.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventUpdateDateDto(@NotNull @FutureOrPresent @JsonFormat(pattern = "yyyy-MM-dd HH:mm")LocalDateTime date) {
}

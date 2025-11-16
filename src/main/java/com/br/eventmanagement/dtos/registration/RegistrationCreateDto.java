package com.br.eventmanagement.dtos.registration;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RegistrationCreateDto(@NotNull UUID userId, @NotNull UUID eventId) {
}

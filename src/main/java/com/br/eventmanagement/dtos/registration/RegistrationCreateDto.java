package com.br.eventmanagement.dtos.registration;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegistrationCreateDto(@NotNull UUID userId, @NotNull UUID eventId) {
}

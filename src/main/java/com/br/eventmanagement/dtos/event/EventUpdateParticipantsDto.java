package com.br.eventmanagement.dtos.event;

import jakarta.validation.constraints.NotNull;

public record EventUpdateParticipantsDto(@NotNull Integer maxParticipants) {
}

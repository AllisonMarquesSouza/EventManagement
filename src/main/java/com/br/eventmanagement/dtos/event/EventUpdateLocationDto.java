package com.br.eventmanagement.dtos.event;

import jakarta.validation.constraints.NotBlank;

public record EventUpdateLocationDto(@NotBlank String location) {
}

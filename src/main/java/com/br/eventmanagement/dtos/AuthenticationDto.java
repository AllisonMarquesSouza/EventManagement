package com.br.eventmanagement.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDto(@NotBlank String username, @NotBlank String password) {
}

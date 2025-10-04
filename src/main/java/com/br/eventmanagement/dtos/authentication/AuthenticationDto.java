package com.br.eventmanagement.dtos.authentication;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDto(@NotBlank String username, @NotBlank String password) {
}

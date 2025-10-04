package com.br.eventmanagement.dtos.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(@NotBlank String username, @NotBlank @Size(min = 12) String password, @NotBlank @Email String email) {
}

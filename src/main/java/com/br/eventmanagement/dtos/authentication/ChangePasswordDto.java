package com.br.eventmanagement.dtos.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(@NotBlank
                                String oldPassword,
                                @NotBlank @Size(min = 12, max = 60)
                                String newPassword) {
}

package com.br.eventmanagement.controllers;

import com.br.eventmanagement.dtos.authentication.AuthenticationDto;
import com.br.eventmanagement.dtos.authentication.ChangePasswordDto;
import com.br.eventmanagement.dtos.authentication.RegisterDto;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.enums.UserRole;
import com.br.eventmanagement.security.TokenService;
import com.br.eventmanagement.services.AuthenticationService;
import com.br.eventmanagement.services.EventService;
import com.br.eventmanagement.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("login() - should make login when successful")
    void login_shouldMakeLoginWhenSuccessful() throws Exception {
        AuthenticationDto authenticationDto = new AuthenticationDto("allison", "password123");

        when(authenticationService.login(any(AuthenticationDto.class))).thenReturn("token-jwt");

        mockMvc.perform( post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"));
    }

    @Test
    @DisplayName("register() - should make register when successful")
    void register_shouldMakeRegisterWhenSuccessful() throws Exception {
        User user = User.builder()
                .id(UUID.fromString("fa9e7123-bf40-454d-8820-6e6dce717c8d"))
                .username("allison")
                .email("allison@gmail.com")
                .password("password123")
                .role(UserRole.PARTICIPANT)
                .createdAt(LocalDateTime.now())
                .build();

        RegisterDto registerDto = new RegisterDto("allison", "password1234", "allison@gmail.com");

        when(authenticationService.register(any(RegisterDto.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    @DisplayName("changePassword() - should change password when successful")
    void changePassword_shouldChangePasswordWhenSuccessful() throws Exception{
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("old-password", "new-password");

        doNothing().when(authenticationService).changePassword(any(ChangePasswordDto.class));

        mockMvc.perform(patch("/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDto))
        ).andExpect(status().isNoContent());
    }

}
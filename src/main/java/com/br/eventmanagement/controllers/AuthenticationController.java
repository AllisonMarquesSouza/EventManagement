package com.br.eventmanagement.controllers;

import com.br.eventmanagement.dtos.authentication.AuthenticationDto;
import com.br.eventmanagement.dtos.authentication.ChangePasswordDto;
import com.br.eventmanagement.dtos.authentication.RegisterDto;
import com.br.eventmanagement.dtos.authentication.TokenDto;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid AuthenticationDto authDto){
        String token = authenticationService.login(authDto);
        return ResponseEntity.ok(new TokenDto(token));

    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterDto registerDto){
        return new ResponseEntity<>(authenticationService.register(registerDto), HttpStatus.CREATED);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto){
        authenticationService.changePassword(changePasswordDto);
        return ResponseEntity.noContent().build();
    }
}

package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.authentication.AuthenticationDto;
import com.br.eventmanagement.dtos.authentication.ChangePasswordDto;
import com.br.eventmanagement.dtos.authentication.RegisterDto;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.security.TokenService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthenticationService(@Lazy AuthenticationManager authenticationManager, TokenService tokenService, UserService userService){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }


    public String login(AuthenticationDto authDto){
        var usernamePassword = new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password());

        var auth = authenticationManager.authenticate(usernamePassword);
        return tokenService.generateToken((User) auth.getPrincipal());
    }


    public User register(RegisterDto registerDto){
        return userService.register(registerDto);
    }

    public void changePassword(ChangePasswordDto changePasswordDto){
        userService.changePassword(changePasswordDto);
    }
}

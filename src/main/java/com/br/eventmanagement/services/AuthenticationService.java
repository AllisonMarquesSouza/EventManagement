package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.authentication.AuthenticationDto;
import com.br.eventmanagement.dtos.authentication.RegisterDto;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.security.TokenService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    //using lazy here, to the AuthenticationManager do not get in loop with the same from SecurityConfiguration
    public AuthenticationService(@Lazy AuthenticationManager authenticationManager, TokenService tokenService, UserService userService){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

/*
* When you use the authenticate the spring already do the entire process of verification
 going to the database and making the verification between the data typed and the data stored.

* Basically the PasswordEncoder method in SecurityConfig does the verification of the hash between passwords in the
    authentication when you call the authenticate, like below

* put try-catch in the exception threw by this method "authenticate"
 */
    public String login(AuthenticationDto authDto){
        var usernamePassword = new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password());

        //I removed the try-catch because I'm handling exceptions already
        var auth = authenticationManager.authenticate(usernamePassword);
        return tokenService.generateToken((User) auth.getPrincipal());
    }

    @Transactional
    public User register(RegisterDto registerDto){
        return userService.register(registerDto);
    }

}

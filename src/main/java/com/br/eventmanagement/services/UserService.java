package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.authentication.ChangePasswordDto;
import com.br.eventmanagement.dtos.authentication.RegisterDto;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.enums.UserRole;
import com.br.eventmanagement.exceptions.BadRequestException;
import com.br.eventmanagement.exceptions.EntityAlreadyExistsException;
import com.br.eventmanagement.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public User getById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public User register(RegisterDto registerDto){
        if(userRepository.findByUsername(registerDto.username())!= null) {
            throw new EntityAlreadyExistsException("Username already exists");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());

        User newUser = new User(registerDto.username(), encryptedPassword, registerDto.email());
        newUser.setRole(UserRole.PARTICIPANT);
        return userRepository.save(newUser);
    }

    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto){
        User currentUser = this.getCurrentUser();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(changePasswordDto.oldPassword(), currentUser.getPassword())){
            currentUser.setPassword(encoder.encode(changePasswordDto.newPassword()));
            userRepository.save(currentUser);
            return;
        }
        throw new BadRequestException("Error while verifying the passwords");
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return (User) userDetails;
        }
        throw new BadRequestException("User is not authenticated");
    }

}

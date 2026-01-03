package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.authentication.ChangePasswordDto;
import com.br.eventmanagement.dtos.authentication.RegisterDto;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.enums.UserRole;
import com.br.eventmanagement.exceptions.BadRequestException;
import com.br.eventmanagement.exceptions.EntityAlreadyExistsException;
import com.br.eventmanagement.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.fromString("fa7970df-eaed-4bfc-a970-638017ee8f6a"))
                .username("allison")
                .email("allison@gmail.com")
                .password("allison1234")
                .role(UserRole.PARTICIPANT)
                .createdAt(LocalDateTime.of(2025, 10, 15, 10, 30))
                .build();
    }

    @Test
    @DisplayName("getById() - Should return user by id when successful")
    void getById_shouldReturnUserByIdWhenSuccessful(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getById(this.user.getId());

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("getById() - Should throw EntityNotFoundException when user not found")
    void getById_shouldThrowEntityNotFoundExceptionWhenUserNotFound(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(user.getId())
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("register() - Should register a user when successful")
    void register_shouldRegisterUserWhenSuccessful(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(new RegisterDto(user.getUsername(), user.getPassword(), user.getEmail()));

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register() - Should throw EntityAlreadyExistsException when user already exists")
    void register_shouldThrowEntityAlreadyExistsExceptionWhenUserAlreadyExists(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        EntityAlreadyExistsException exception = assertThrows(
                EntityAlreadyExistsException.class,
                () -> userService.register(new RegisterDto(user.getUsername(), user.getPassword(), user.getEmail()))
        );

        assertEquals("User with such username already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    @DisplayName("changePassword() - Should change password when successful without security context")
    void changePassword_shouldChangePasswordWhenSuccessfulWithoutSecurityContext() {

        User userCurrent = User.builder()
                .id(UUID.randomUUID())
                .username("allison")
                .password("encoded-old")
                .email("testing@gmail.com")
                .build();

        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto("old123", "new123");

        // spy allows overriding only THIS method, basically ignore the behavior and do as I want
        // it will ignore the spring security context of getting the current user, just return the one I specified
        UserService spyService = spy(userService);
        doReturn(userCurrent).when(spyService).getCurrentUser();

        when(passwordEncoder.matches(changePasswordDto.oldPassword(), userCurrent.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordDto.newPassword())).thenReturn("encoded-new");

        spyService.changePassword(changePasswordDto);

        assertEquals("encoded-new", userCurrent.getPassword());
        verify(userRepository, times(1)).save(userCurrent);
    }

    @Test
    @DisplayName("changePassword() - Should throw BadRequestException when the passwords don't matches")
    void changePassword_shouldThrowBadRequestExceptionWhenPasswordsDontMatches(){
        User userCurrent = User.builder()
                .id(UUID.randomUUID())
                .username("allison")
                .password("wrong-encoded")
                .email("testing@gmail.com")
                .build();

        ChangePasswordDto changePasswordDto =
                new ChangePasswordDto("old123", "new123");

        UserService spyService = spy(userService);
        doReturn(userCurrent).when(spyService).getCurrentUser();

        when(passwordEncoder.matches(changePasswordDto.oldPassword(), userCurrent.getPassword())).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> spyService.changePassword(changePasswordDto)
        );

        assertEquals("Error while verifying the passwords", exception.getMessage());
        verifyNoInteractions(userRepository);
    }
}
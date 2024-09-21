package org.library.libraryback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.libraryback.dto.LoginRequest;
import org.library.libraryback.dto.User;
import org.library.libraryback.repository.UserRepository;
import org.library.libraryback.service.AuthService;
import org.library.libraryback.utility.JwtTokenProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("$2a$10$..."); // Example hashed password

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken("user@example.com")).thenReturn("jwt.token.here");

        String token = authService.login(loginRequest);

        assertEquals("jwt.token.here", token);
        verify(userRepository).findByEmail("user@example.com");
        verify(passwordEncoder).matches("password", user.getPassword());
        verify(jwtTokenProvider).generateToken("user@example.com");
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testLogin_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("wrongpassword");

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("$2a$10$..."); // Example hashed password

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail("user@example.com");
        verify(passwordEncoder).matches("wrongpassword", user.getPassword());
    }
}

package org.library.libraryback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.libraryback.dto.LoginRequest;
import org.library.libraryback.dto.User;
import org.library.libraryback.dto.UserAuthenticationResponse;
import org.library.libraryback.dto.UserRegistrationDTO;
import org.library.libraryback.repository.UserRepository;
import org.library.libraryback.service.AuthService;
import org.library.libraryback.utility.JwtTokenProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthServiceTests {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

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
        String email = "user@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User();
        user.setEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(password), eq(encodedPassword))).thenReturn(true);
        when(jwtTokenProvider.generateToken(email)).thenReturn("mockToken");

        UserAuthenticationResponse response = authService.login(loginRequest);

        assertEquals(email, response.getUser().getEmail());
        assertEquals("mockToken", response.getToken());
    }

    @Test
    public void testLogin_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("invalid@example.com", "wrongPassword");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    public void testSaveUser_Success() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO("First", "Last", "user@example.com", "password", "USER");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword"); // Mock password encoding

        ResponseEntity<String> response = authService.saveUser(userDTO);

        assertEquals(null, response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testSaveUser_UserAlreadyExists() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO("First", "Last", "user@example.com", "password", "USER");
        User existingUser = new User();
        existingUser.setEmail(userDTO.getEmail());

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(existingUser));
        ResponseEntity<String> response = authService.saveUser(userDTO);

        assertEquals(ResponseEntity.badRequest().body("User already exists"), response);
        verify(userRepository, times(0)).save(any(User.class)); // Ensure save is NOT called
    }
}
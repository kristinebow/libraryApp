package org.library.libraryback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.libraryback.controller.AuthController;
import org.library.libraryback.dto.AuthResponse;
import org.library.libraryback.dto.LoginRequest;
import org.library.libraryback.dto.User;
import org.library.libraryback.dto.UserAuthenticationResponse;
import org.library.libraryback.dto.UserRegistrationDTO;
import org.library.libraryback.service.AuthService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

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
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        String token = "mockToken";
        String userRole = "USER";

        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setRole(userRole);

        UserAuthenticationResponse mockResponse = new UserAuthenticationResponse();
        mockResponse.setToken(token);
        mockResponse.setUser(mockUser);

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> responseEntity = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AuthResponse body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals(token, body.getToken());
        assertEquals(loginRequest.getEmail(), body.getEmail());
        assertEquals(userRole, body.getRole());
    }

    @Test
    public void testRegisterUser_Success() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO("firstName", "lastName", "user@example.com", "password", "USER");
        when(authService.saveUser(any(UserRegistrationDTO.class))).thenReturn(ResponseEntity.ok("User registered successfully"));

        ResponseEntity<?> responseEntity = authController.registerUser(userDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testRegisterUser_Failure() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO("firstName", "lastName", "user@example.com", "password", "USER");
        when(authService.saveUser(any(UserRegistrationDTO.class))).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed"));

        ResponseEntity<?> responseEntity = authController.registerUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
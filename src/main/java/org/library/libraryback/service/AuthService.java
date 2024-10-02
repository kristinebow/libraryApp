package org.library.libraryback.service;

import org.library.libraryback.dto.LoginRequest;
import org.library.libraryback.dto.User;
import org.library.libraryback.dto.UserAuthenticationResponse;
import org.library.libraryback.dto.UserRegistrationDTO;
import org.library.libraryback.repository.UserRepository;
import org.library.libraryback.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserAuthenticationResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordMatches(loginRequest.getPassword(), user.getPassword())) {
                // Generate and return JWT token
                String token = jwtTokenProvider.generateToken(user.getEmail());
                return new UserAuthenticationResponse(user, token);
            }
        }
        throw new RuntimeException("Invalid credentials");
    }


    public ResponseEntity<String> saveUser(UserRegistrationDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return null;
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


}

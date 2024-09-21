package org.library.libraryback.service;

import org.library.libraryback.dto.LoginRequest;
import org.library.libraryback.dto.User;
import org.library.libraryback.repository.UserRepository;
import org.library.libraryback.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordMatches(loginRequest.getPassword(), user.getPassword())) {
                // Generate and return JWT token
                return jwtTokenProvider.generateToken(user.getEmail());
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


}

package org.library.libraryback.controller;

import org.library.libraryback.dto.*;
import org.library.libraryback.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserAuthenticationResponse response = authService.login(loginRequest);
        String userRole = "";
        if (response.getUser() != null) {
            userRole = response.getUser().getRole();
        }
        return ResponseEntity.ok(new AuthResponse(response.getToken(), loginRequest.getEmail(), userRole));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        ResponseEntity<String> saveUserResponse = authService.saveUser(userDTO);
        if (saveUserResponse != null) return saveUserResponse;
        return ResponseEntity.ok(HttpStatus.OK);
    }
}

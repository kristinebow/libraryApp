package org.library.libraryback.dto;

import lombok.Getter;

@Getter
public class AuthResponse {
    private String token;
    private String email;

    public AuthResponse(String token, String email) {
        this.token = token;
        this.email = email;
    }

}
package org.library.libraryback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String mail, String password) {
        this.email = mail;
        this.password = password;
    }
}

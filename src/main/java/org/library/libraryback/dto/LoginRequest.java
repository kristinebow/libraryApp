package org.library.libraryback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;

    public LoginRequest(String mail, String password) {
        this.email = mail;
        this.password = password;
    }
}

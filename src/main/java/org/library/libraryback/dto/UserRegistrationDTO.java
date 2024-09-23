package org.library.libraryback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    public UserRegistrationDTO(String mail, String first, String last, String password) {
        this.firstName = first;
        this.lastName = last;
        this.email = mail;
        this.password = password;
    }
}

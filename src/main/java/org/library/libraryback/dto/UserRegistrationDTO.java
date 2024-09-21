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
}

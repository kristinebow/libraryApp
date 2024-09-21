package org.library.libraryback.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

   public User(String email, String password) {
       this.email = email;
       this.password = password;
   }

    public User(String user, String password, ArrayList<Object> objects) {
    }
}

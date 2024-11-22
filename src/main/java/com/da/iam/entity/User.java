package com.da.iam.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    private String password;
    private String phone;
    private LocalDate dob;
    private String image;

    @Column(name = "is_confirm")
    private boolean isConfirm;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        isConfirm = false;
    }


}

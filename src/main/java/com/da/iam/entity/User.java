package com.da.iam.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;


@Setter
@Getter
@Entity @Table(name = "users")
public class User {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;


    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private String email;
    private String password;
    private String phone;
    private LocalDate dob;
    private String image;

    public User() {
    }

    public User(String email, String password, String phone, LocalDate dob, String image) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.image = image;
    }

}

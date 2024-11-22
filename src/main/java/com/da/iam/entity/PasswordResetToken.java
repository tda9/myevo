package com.da.iam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String token;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "created_at", updatable = false, insertable = true)
    private LocalDateTime createdAt;
    @Column(name = "user_id")
    private Long userId;
    public PasswordResetToken(String token, LocalDateTime expiryDate, Long userId) {
        this.token = token;
        this.expirationDate = expiryDate;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}

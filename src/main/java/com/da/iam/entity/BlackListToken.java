package com.da.iam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blacklist_token")
@AllArgsConstructor
@NoArgsConstructor
public class BlackListToken {

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

    public BlackListToken(String token, LocalDateTime expirationDate, LocalDateTime createdAt, Long userId){
        this.token=token;
        this.expirationDate=expirationDate;
        this.createdAt=createdAt;
        this.userId=userId;
    }
}

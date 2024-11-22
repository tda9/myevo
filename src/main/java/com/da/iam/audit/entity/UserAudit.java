package com.da.iam.audit.entity;

import com.da.iam.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Table(name = "user_audit")
@Entity
public class UserAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long id;
    @Column(name = "user_agent")
    private String userAgent;
    private String ip;
    @Column(name = "request_method")
    private String requestMethod;
    private String url;
    @Column(name = "user_id")
    private Long userId;
    private String email;
    private String password;
    private String phone;
    private LocalDate dob;
    private String image;
    @CreatedDate // Automatically set the creation timestamp
    @Column(name = "change_time")
    private LocalDateTime changeTime;

    public UserAudit() {
    }
}

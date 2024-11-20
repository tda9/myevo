package com.da.iam.service;

import com.da.iam.entity.PasswordResetToken;
import com.da.iam.entity.User;
import com.da.iam.repo.PasswordResetTokenRepo;
import com.da.iam.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@AllArgsConstructor
@Service

public class UserService {
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public User getUser(Long id) {
        return userRepo.findById(id).orElseThrow();
    }

    public void changePassword(String currentPassword, String newPassword, String confirmPassword, String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            System.out.println(user.getPassword()+"---------------------------"+passwordEncoder.matches(currentPassword, user.getPassword()));
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    public void forgotPassword(String to) {
        User user = userRepo.findByEmail(to).orElseThrow();
        String token = generateToken();
        emailService.sendEmail(to, "Confirm password reset", "Your token is:"+ token, null);
        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(100), user));

    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void resetPassword(String email,String newPassword, String token) {
        User user = userRepo.findByEmail(email).orElseThrow();
        PasswordResetToken requestToken = passwordResetTokenRepo.findPasswordResetTokenByToken(token);
        System.out.println(email+"-----------"+requestToken.getUser().getUserId()+"-----------"+requestToken.getExpiryDate() + user.getEmail());
        if (requestToken.getExpiryDate().isBefore(LocalDateTime.now()) || user.getUserId()!=(requestToken.getUser().getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        passwordResetTokenRepo.delete(requestToken);
    }

}

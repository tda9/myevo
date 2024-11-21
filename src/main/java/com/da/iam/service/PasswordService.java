package com.da.iam.service;

import com.da.iam.entity.PasswordResetToken;
import com.da.iam.entity.User;
import com.da.iam.repo.PasswordResetTokenRepo;
import com.da.iam.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class PasswordService {
    private final UserRepo userRepo;
    private final EmailService emailService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
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
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    public void forgotPassword(String to) {
        User user = userRepo.findByEmail(to).orElseThrow();
        String token = generateToken();
        //emailService.sendEmail(to, "Confirm password reset", "Your token is:"+ token, null);
        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(10), user));

    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void resetPassword(String email, String newPassword, String token) {
        User user = userRepo.findByEmail(email).orElseThrow();
        PasswordResetToken requestToken = passwordResetTokenRepo.findPasswordResetTokenByToken(token);
        if (requestToken.getExpiryDate().isBefore(LocalDateTime.now()) || user.getUserId() != (requestToken.getUser().getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        passwordResetTokenRepo.delete(requestToken);
    }


}

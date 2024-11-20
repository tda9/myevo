package com.da.iam.repo;

import com.da.iam.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findPasswordResetTokenByUser_Email(String email);
    PasswordResetToken findPasswordResetTokenByToken(String token);
}

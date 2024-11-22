package com.da.iam.repo;

import com.da.iam.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findPasswordResetTokenByToken(String token);
    Optional<PasswordResetToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}

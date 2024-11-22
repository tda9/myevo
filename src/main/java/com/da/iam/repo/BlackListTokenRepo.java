package com.da.iam.repo;

import com.da.iam.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListTokenRepo extends JpaRepository<BlackListToken,Long> {

    Optional<BlackListToken> findTopByUserIdOrderByCreatedAtDesc(Long id);
void deleteAllByUserId(Long id);
}

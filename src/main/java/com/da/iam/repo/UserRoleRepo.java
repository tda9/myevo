package com.da.iam.repo;

import com.da.iam.entity.Role;
import com.da.iam.entity.User;
import com.da.iam.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public interface UserRoleRepo extends JpaRepository<Role, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id) " +
            "VALUES (:userId, :roleId)", nativeQuery = true)
    void saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_roles ur WHERE ur.user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}

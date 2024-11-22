package com.da.iam.audit.repo;

import com.da.iam.audit.entity.UserAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuditRepository extends JpaRepository<UserAudit,Long> {
}

package com.leadplatform.api.repository;

import com.leadplatform.api.entity.LeadRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadRequestRepository extends JpaRepository<LeadRequestEntity, Long> {
    Optional<LeadRequestEntity> findByLeadId(String leadId);
    boolean existsByLeadId(String leadId);
}

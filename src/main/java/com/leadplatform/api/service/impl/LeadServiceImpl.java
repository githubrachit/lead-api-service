package com.leadplatform.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadplatform.api.dto.LeadRequest;
import com.leadplatform.api.dto.LeadResponse;
import com.leadplatform.api.dto.LeadStatusResponse;
import com.leadplatform.api.entity.LeadRequestEntity;
import com.leadplatform.api.exception.LeadNotFoundException;
import com.leadplatform.api.repository.LeadRequestRepository;
import com.leadplatform.api.service.LeadService;
import com.leadplatform.api.service.SnsPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRequestRepository leadRequestRepository;
    private final SnsPublisherService snsPublisherService;
    private final ObjectMapper objectMapper;

    public LeadServiceImpl(LeadRequestRepository leadRequestRepository,
                           SnsPublisherService snsPublisherService,
                           ObjectMapper objectMapper) {
        this.leadRequestRepository = leadRequestRepository;
        this.snsPublisherService = snsPublisherService;
        this.objectMapper = objectMapper;
    }

    @Override
    public LeadResponse createLead(LeadRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Creating lead. leadId={}, correlationId={}", request.getLeadId(), correlationId);

        try {
            String payload = objectMapper.writeValueAsString(request);
            LeadRequestEntity entity = LeadRequestEntity.builder()
                    .leadId(request.getLeadId())
                    .requestPayload(payload)
                    .status("ACCEPTED")
                    .retryCount(0)
                    .build();
            leadRequestRepository.save(entity);
            snsPublisherService.publish(request, correlationId);
        } catch (Exception e) {
            log.info("Lead may already exist or sns publish failed. leadId={}, error={}", request.getLeadId(), e.getMessage());
            return LeadResponse.builder()
                    .requestId(correlationId)
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();
        }

        return LeadResponse.builder()
                .requestId(correlationId)
                .status("ACCEPTED")
//                .message("Lead request accepted for processing")
                .build();
    }

    @Override
    public LeadStatusResponse getLeadStatus(String leadId) {
        log.info("Fetching lead status. leadId={}", leadId);

        LeadRequestEntity entity = leadRequestRepository.findByLeadId(leadId)
                .orElseThrow(() -> new LeadNotFoundException(leadId));

        return LeadStatusResponse.builder()
                .leadId(entity.getLeadId())
                .status(entity.getStatus())
                .retryCount(entity.getRetryCount())
                .lastUpdated(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }

    @Override
    public LeadResponse retryLead(String leadId) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Retrying lead. leadId={}, correlationId={}", leadId, correlationId);

        LeadRequestEntity entity = leadRequestRepository.findByLeadId(leadId)
                .orElseThrow(() -> new LeadNotFoundException(leadId));

        if (!"FAILED".equals(entity.getStatus())) {
            throw new IllegalStateException("Only FAILED leads can be retried. Current status: " + entity.getStatus());
        }

        try {
            LeadRequest originalRequest = objectMapper.readValue(entity.getRequestPayload(), LeadRequest.class);

            entity.setStatus("RETRY_INITIATED");
            leadRequestRepository.save(entity);

            snsPublisherService.publish(originalRequest, correlationId);

            return LeadResponse.builder()
                    .requestId(correlationId)
                    .status("RETRY_INITIATED")
//                    .message("Lead retry initiated")
                    .build();

        } catch (Exception e) {
            log.error("Failed to retry lead. leadId={}, error={}", leadId, e.getMessage());
            throw new RuntimeException("Failed to retry lead", e);
        }
    }
}

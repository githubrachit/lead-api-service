package com.leadplatform.api.service;

import com.leadplatform.api.dto.LeadRequest;

/**
 * Service interface for publishing lead events to AWS SNS.
 */
public interface SnsPublisherService {

    String publish(LeadRequest leadRequest, String correlationId);
}

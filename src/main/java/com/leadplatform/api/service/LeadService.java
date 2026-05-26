package com.leadplatform.api.service;

import com.leadplatform.api.dto.LeadRequest;
import com.leadplatform.api.dto.LeadResponse;
import com.leadplatform.api.dto.LeadStatusResponse;

/**
 * Service interface for lead management operations.
 */
public interface LeadService {

    LeadResponse createLead(LeadRequest request);

    LeadStatusResponse getLeadStatus(String leadId);

    LeadResponse retryLead(String leadId);
}

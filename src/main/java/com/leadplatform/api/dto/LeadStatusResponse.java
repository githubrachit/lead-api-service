package com.leadplatform.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadStatusResponse {
    private String leadId;
    private String status;
    private int retryCount;
    private String lastUpdated;
}

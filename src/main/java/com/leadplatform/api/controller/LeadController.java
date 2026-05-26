package com.leadplatform.api.controller;

import com.leadplatform.api.dto.LeadRequest;
import com.leadplatform.api.dto.LeadResponse;
import com.leadplatform.api.dto.LeadStatusResponse;
import com.leadplatform.api.service.LeadService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leads")
@Slf4j
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody LeadRequest request) {
        log.info("POST /api/v1/leads - leadId={}", request.getLeadId());
        LeadResponse response = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{leadId}")
    public ResponseEntity<LeadStatusResponse> getLeadStatus(@PathVariable String leadId) {
        log.info("GET /api/v1/leads/{}", leadId);
        LeadStatusResponse response = leadService.getLeadStatus(leadId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{leadId}/retry")
    public ResponseEntity<LeadResponse> retryLead(@PathVariable String leadId) {
        log.info("POST /api/v1/leads/{}/retry", leadId);
        LeadResponse response = leadService.retryLead(leadId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

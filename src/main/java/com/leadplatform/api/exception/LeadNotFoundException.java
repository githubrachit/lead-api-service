package com.leadplatform.api.exception;

public class LeadNotFoundException extends RuntimeException {
    public LeadNotFoundException(String leadId) {
        super("Lead not found: " + leadId);
    }
}

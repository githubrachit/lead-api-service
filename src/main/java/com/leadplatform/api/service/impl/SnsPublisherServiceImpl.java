package com.leadplatform.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadplatform.api.dto.LeadRequest;
import com.leadplatform.api.service.SnsPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SnsPublisherServiceImpl implements SnsPublisherService {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final String topicArn;

    public SnsPublisherServiceImpl(SnsClient snsClient,
                                   ObjectMapper objectMapper,
                                   @Value("${app.sns.topic-arn}") String topicArn) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
    }

    @Override
    public String publish(LeadRequest leadRequest, String correlationId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("correlationId", correlationId);
            message.put("leadId", leadRequest.getLeadId());
            message.put("name", leadRequest.getName());
            message.put("mobile", leadRequest.getMobile());
            message.put("email", leadRequest.getEmail());

            String messageBody = objectMapper.writeValueAsString(message);

            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(messageBody)
                    .subject("LeadCreated")
                    .build();

            PublishResponse response = snsClient.publish(publishRequest);

            log.info("Published to SNS. correlationId={}, leadId={}, messageId={}",
                    correlationId, leadRequest.getLeadId(), response.messageId());

            return response.messageId();

        } catch (Exception e) {
            log.info("Failed to publish to SNS. correlationId={}, leadId={}, error={}",
                    correlationId, leadRequest.getLeadId(), e.getMessage());
            throw new RuntimeException("Failed to publish lead event to SNS", e);
        }
    }
}

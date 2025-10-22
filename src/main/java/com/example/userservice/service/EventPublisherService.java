package com.example.userservice.service;

import com.example.userservice.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    private static final String TOPIC = "user-events";
    
    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;
    
    public void publishUserEvent(String operation, String email, String username) {
        try {
            UserEventDto event = new UserEventDto(operation, email, username);
            kafkaTemplate.send(TOPIC, event);
            logger.info("Published user event: {}", event);
        } catch (Exception e) {
            logger.error("Failed to publish user event for user: {}", email, e);
        }
    }
}

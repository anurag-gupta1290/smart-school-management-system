package com.smart_school_management_system.smart_school_2026.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // ✅ Ye function call hoga jab Teacher naya data upload karega
    public void sendNotification(Long classId, String title, String message) {
        // Student ko specific class ke topic par notification bhejo
        messagingTemplate.convertAndSend("/topic/class/" + classId,
                Map.of(
                        "title", title,
                        "message", message,
                        "timestamp", System.currentTimeMillis()
                )
        );
    }
}
package com.lms.LeaveManagementSystem.service.impl;

import com.lms.LeaveManagementSystem.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendNotification(String message, Long userId) {
        // For demonstration, simply print the notification.
        System.out.println("Notification for user " + userId + ": " + message);
    }
}

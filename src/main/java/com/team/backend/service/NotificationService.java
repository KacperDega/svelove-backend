package com.team.backend.service;

import com.team.backend.model.Enum.NotificationType;
import com.team.backend.model.Notification;
import com.team.backend.model.User;
import com.team.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final int MAX_NOTIFICATIONS_PER_USER = 20;

    private final NotificationRepository notificationRepository;

    public void createNotification(User user, NotificationType type, String message, Long referenceId) {
        Notification n = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .read(false)
                .build();

        notificationRepository.save(n);

        notificationRepository.deleteAllExceptLatestNByUser(user.getId(), MAX_NOTIFICATIONS_PER_USER);
    }

    public List<Notification> getLatestNotifications(User user) {
        return notificationRepository.findTop20ByUserOrderByCreatedAtDesc(user);
    }

    public void markAsRead(Long id, User user) {
        Notification notification = notificationRepository.findById(id)
                .filter(n -> n.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}



package com.team.backend.model.mapper;

import com.team.backend.model.Notification;
import com.team.backend.model.dto.NotificationDto;

public class NotificationMapper {

    public static NotificationDto mapToNotificationDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getMessage(),
                n.getType().name(),
                n.getReferenceId(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}

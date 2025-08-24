package com.team.backend.model.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String message,
        String type,
        Long referenceId,
        boolean read,
        LocalDateTime createdAt
) {}
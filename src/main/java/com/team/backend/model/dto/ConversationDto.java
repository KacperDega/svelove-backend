package com.team.backend.model.dto;

import java.time.LocalDateTime;

public record ConversationDto(
        Long matchId,
        String otherUserName,
        String lastMessageContent,
        LocalDateTime lastMessageTimestamp,
        String photoUrl
) {}


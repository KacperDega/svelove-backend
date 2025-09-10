package com.team.backend.model.mapper;

import com.team.backend.model.Match;
import com.team.backend.model.Message;
import com.team.backend.model.User;
import com.team.backend.model.dto.ConversationDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ConversationMapper {

    public static ConversationDto toConversationDto(Match match, User user, Message lastMessage) {
        User otherUser = match.getFirstUser().equals(user) ? match.getSecondUser() : match.getFirstUser();

        LocalDateTime lastMessageTimestamp = null;
        if (lastMessage != null) {
            lastMessageTimestamp = Instant.ofEpochMilli(lastMessage.getTimestamp())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        String photoUrl = null;
        if (otherUser.getPhotoUrls() != null && !otherUser.getPhotoUrls().isEmpty()) {
            photoUrl = otherUser.getPhotoUrls().get(0);
        }

        return new ConversationDto(
                match.getId(),
                otherUser.getUsername(),
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessageTimestamp,
                photoUrl
        );
    }
}



package com.team.backend.service;

import com.team.backend.model.Match;
import com.team.backend.model.Message;
import com.team.backend.model.User;
import com.team.backend.model.dto.MessageRequestDto;
import com.team.backend.model.dto.MessageResponseDto;
import com.team.backend.model.mapper.MessageMapper;
import com.team.backend.repository.MatchRepository;
import com.team.backend.repository.MessageRepository;
import com.team.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ChatService {

    private final MatchRepository matchRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserStatsService userStatsService;

    public MessageResponseDto processIncomingMessage(Long matchId, MessageRequestDto requestDto) {
        User sender;
        try {
            // Try to get current user from security context
            sender = userService.getCurrentUser();
        } catch (Exception e) {
            // If security context fails, fall back to writer ID from message
            log.warn("Failed to get user from security context, falling back to message writtenBy: {}", requestDto.writtenBy());
            sender = userRepository.findById(requestDto.writtenBy())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + requestDto.writtenBy()));
        }

        Match match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found: " + matchId));


        Message message = MessageMapper.toEntity(requestDto, sender, match);
        log.debug(message.toString());
        Message saved = messageRepository.save(message);

        checkAndMarkConversationStarted(match);

        return MessageMapper.mapToMessageResponse(saved);
    }

    private void checkAndMarkConversationStarted(Match match) {
        if (match.isConversationStarted()) return;

        Set<Long> distinctSenders = match.getMessages().stream()
                .map(Message::getWrittenBy)
                .collect(Collectors.toSet());

        if (distinctSenders.size() == 2) {
            match.setConversationStarted(true);
            matchRepository.save(match);

            userStatsService.recordConversationStarted(match.getFirstUser(), match.getSecondUser());
        }
    }

    public List<MessageResponseDto> getChatMessagesForMatch(Long matchId) {
        Match match = matchRepository.findMatchById(matchId).orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        User currentUser = userService.getCurrentUser();

        if (!match.getFirstUser().getId().equals(currentUser.getId()) &&
                !match.getSecondUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not authorized to view messages for this match.");
        }

        return messageRepository.findByMatchIdOrderByTimestampDesc(matchId).stream()
                .map(MessageMapper::mapToMessageResponse)
                .toList();
    }
}

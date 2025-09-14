package com.team.backend.controller;

import com.team.backend.model.User;
import com.team.backend.model.dto.ConversationDto;
import com.team.backend.model.dto.MessageRequestDto;
import com.team.backend.model.dto.MessageResponseDto;
import com.team.backend.service.ChatService;
import com.team.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final ChatService chatService;

    @MessageMapping("chat/{matchId}")
    public void processMessage(@DestinationVariable String matchId, MessageRequestDto messageRequestDto, Authentication authentication) {
        log.info("Received message for match {}: {}", matchId, messageRequestDto);
        //log.info("Authentication user: {}", ((User) authentication.getPrincipal()).getLogin());

        //User currentUser = userService.getCurrentUser();
        String userLogin = ((User) authentication.getPrincipal()).getLogin();
        User currentUser = userService.getUserByLogin(userLogin);
        log.info("Message sent by authenticated user: {} (ID: {})", currentUser.getLogin(), currentUser.getId());

        MessageResponseDto messageResponseDto = chatService.processIncomingMessage(Long.parseLong(matchId), messageRequestDto, currentUser);

        messagingTemplate.convertAndSend("/topic/messages/" + matchId, messageResponseDto);

        log.info("Received and saved message to match {}: {}", matchId, messageResponseDto);
    }


    @GetMapping("/{matchId}")
    public ResponseEntity<List<MessageResponseDto>> getMessages(@PathVariable Long matchId) {
        List<MessageResponseDto> messages = chatService.getChatMessagesForMatch(matchId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getUserConversations(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

        List<ConversationDto> conversations = chatService.getUserConversationsWithLastMessage(currentUser);

        return ResponseEntity.ok(conversations);
    }
}

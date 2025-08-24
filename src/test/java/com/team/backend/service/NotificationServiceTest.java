package com.team.backend.service;

import com.team.backend.model.Enum.NotificationType;
import com.team.backend.model.Notification;
import com.team.backend.model.User;
import com.team.backend.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        notificationService = new NotificationService(notificationRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }


    @Test
    void createNotification_shouldCallSaveAndTrim() {
        notificationService.createNotification(user, NotificationType.NEW_MATCH, "Masz nowy match!", 123L);

        verify(notificationRepository).save(any(Notification.class));
        verify(notificationRepository).deleteAllExceptLatestNByUser(user.getId(), 20);
    }

    @Test
    void getLatestNotifications_shouldReturnFromRepository() {
        Notification n1 = Notification.builder().id(1L).user(user).message("Notification1").build();
        Notification n2 = Notification.builder().id(2L).user(user).message("Notification2").build();
        when(notificationRepository.findTop20ByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getLatestNotifications(user);

        assertThat(result).containsExactly(n1, n2);
    }

    @Test
    void markAsRead_shouldUpdateNotificationIfOwnedByUser() {
        Notification notification = Notification.builder()
                .id(1L)
                .user(user)
                .read(false)
                .build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L, user);

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_shouldThrowIfNotificationNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(99L, user));
    }

    @Test
    void markAsRead_shouldThrowIfNotificationBelongsToAnotherUser() {
        User other = new User();
        other.setId(2L);

        Notification notification = Notification.builder()
                .id(1L)
                .user(other)
                .read(false)
                .build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(1L, user));
    }

    @Test
    void markAllAsRead_shouldUpdateAllUnread() {
        Notification n1 = Notification.builder().id(1L).user(user).read(false).build();
        Notification n2 = Notification.builder().id(2L).user(user).read(false).build();
        when(notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user))
                .thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead(user);

        assertThat(n1.isRead()).isTrue();
        assertThat(n2.isRead()).isTrue();
        verify(notificationRepository).saveAll(List.of(n1, n2));
    }
}


package com.team.backend.repository;

import com.team.backend.model.Enum.NotificationType;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Notification;
import com.team.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("testuser");
        user.setLogin("login123");
        user.setPassword("pass123");
        user.setSex(Sex.MALE);
        user.setPreference(Preference.WOMEN);
        user.setDescription("desc");
        user.setAge(25);
        user.setAge_min(18);
        user.setAge_max(30);
        entityManager.persist(user);
        entityManager.flush();
    }


    @Test
    void deleteAllExceptLatestNByUser_shouldKeepOnly20Newest() {
        for (int i = 1; i <= 25; i++) {
            notificationRepository.save(
                    Notification.builder()
                            .user(user)
                            .type(NotificationType.NEW_MATCH)
                            .message("N" + i)
                            .read(false)
                            .createdAt(LocalDateTime.now().plusSeconds(i))
                            .build()
            );
        }

        notificationRepository.deleteAllExceptLatestNByUser(user.getId(), 20);

        List<Notification> remaining = notificationRepository.findAllByUserOrderByIdDesc(user);
        assertThat(remaining).hasSize(20);
        assertThat(remaining.getFirst().getMessage()).isEqualTo("N25");
        assertThat(remaining.getLast().getMessage()).isEqualTo("N6");
    }
}



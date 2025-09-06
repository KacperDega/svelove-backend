package com.team.backend.repository;

import com.team.backend.model.Notification;
import com.team.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    List<Notification> findAllByUserOrderByIdDesc(User user);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM notifications
        WHERE user_id = :userId
        AND id NOT IN (
            SELECT id FROM notifications
            WHERE user_id = :userId
            ORDER BY id DESC
            LIMIT :limit
        )
    """, nativeQuery = true)
    void deleteAllExceptLatestNByUser(@Param("userId") Long userId, @Param("limit") int limit);

    List<Notification> findTop20ByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);
}

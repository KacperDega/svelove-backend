package com.team.backend.repository;

import com.team.backend.model.Notification;
import com.team.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    @Modifying
    @Query("""
       DELETE FROM Notification n
       WHERE n.user = :user
       AND n.id NOT IN (
           SELECT nn.id FROM Notification nn
           WHERE nn.user = :user
           ORDER BY nn.createdAt DESC
           LIMIT 20
       )
    """)
    void deleteAllExceptLatestNByUser(@Param("userId") Long userId, @Param("limit") int limit);

    List<Notification> findTop20ByUserOrderByCreatedAtDesc(User user);
}

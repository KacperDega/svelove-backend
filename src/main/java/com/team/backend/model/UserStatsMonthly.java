package com.team.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "user_stats_monthly",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "year", "month"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UserStatsMonthly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private long leftSwipes = 0;

    @Column(nullable = false)
    private long rightSwipes = 0;

    @Column(nullable = false)
    private long matches = 0;

    @Column(nullable = false)
    private long matchesWithConversation = 0;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public long getTotalSwipes() {
        return leftSwipes + rightSwipes;
    }

    public long getMatchesWithoutMessages() {
        return matches - matchesWithConversation;
    }
}



package com.team.backend.repository;

import com.team.backend.model.Enum.LikedStatus;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Hobby;
import com.team.backend.model.PairStatus;
import com.team.backend.model.PendingPair;
import com.team.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PendingPairRepositoryTest {

    @Autowired
    private PendingPairRepository pendingPairRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = User.of("user1", "login1", "pass", Sex.MALE, Preference.WOMEN,
                "Hello, I'm user1", 25, 20, 30, "Warsaw", List.of(Hobby.fromString("Reading")));

        user2 = User.of("user2", "login2", "pass", Sex.FEMALE, Preference.MEN,
                "Hello, I'm user2", 23, 22, 35, "Krakow", List.of(Hobby.fromString("Cooking")));

        entityManager.persist(user1);
        entityManager.persist(user2);
    }

    @Test
    void shouldFindPendingPairRegardlessOfUserOrder() {
        PairStatus status1 = new PairStatus(user1, LikedStatus.PENDING);
        PairStatus status2 = new PairStatus(user2, LikedStatus.PENDING);
        entityManager.persist(status1);
        entityManager.persist(status2);

        PendingPair pendingPair = new PendingPair(status1, status2);
        entityManager.persist(pendingPair);
        entityManager.flush();

        Optional<PendingPair> found1 = pendingPairRepository.findByUsers(user1, user2);
        assertTrue(found1.isPresent());
        assertEquals(pendingPair.getId(), found1.get().getId());

        Optional<PendingPair> found2 = pendingPairRepository.findByUsers(user2, user1);
        assertTrue(found2.isPresent());
        assertEquals(pendingPair.getId(), found2.get().getId());
    }
}


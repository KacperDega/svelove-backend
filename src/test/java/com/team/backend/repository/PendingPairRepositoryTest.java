package com.team.backend.repository;

import com.team.backend.model.*;
import com.team.backend.model.Enum.LikedStatus;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PendingPairRepositoryTest {

    @Autowired
    private PendingPairRepository pendingPairRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PairStatusRepository pairStatusRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        City warsaw = new City();
        warsaw.setName("Warsaw");
        cityRepository.save(warsaw);

        City krakow = new City();
        krakow.setName("Krakow");
        cityRepository.save(krakow);

        user1 = User.of("user1", "login1", "pass", Sex.MALE, Preference.WOMEN,
                "Hello, I'm user1", 25, 20, 30, warsaw, List.of(Hobby.fromString("Reading")));
        userRepository.save(user1);

        user2 = User.of("user2", "login2", "pass", Sex.FEMALE, Preference.MEN,
                "Hello, I'm user2", 23, 22, 35, krakow, List.of(Hobby.fromString("Cooking")));
        userRepository.save(user2);
    }

    @Test
    void shouldFindPendingPairRegardlessOfUserOrder() {
        PairStatus status1 = new PairStatus(user1, LikedStatus.PENDING);
        pairStatusRepository.save(status1);

        PairStatus status2 = new PairStatus(user2, LikedStatus.PENDING);
        pairStatusRepository.save(status2);

        PendingPair pendingPair = new PendingPair(status1, status2);
        pendingPairRepository.save(pendingPair);

        Optional<PendingPair> found1 = pendingPairRepository.findByUsers(user1, user2);
        assertTrue(found1.isPresent());
        assertEquals(pendingPair.getId(), found1.get().getId());

        Optional<PendingPair> found2 = pendingPairRepository.findByUsers(user2, user1);
        assertTrue(found2.isPresent());
        assertEquals(pendingPair.getId(), found2.get().getId());
    }
}



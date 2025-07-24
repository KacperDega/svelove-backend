package com.team.backend.repository;

import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Hobby;
import com.team.backend.model.Match;
import com.team.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class MatchRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setup() {
        user1 = User.of("user1", "login1", "pass", Sex.MALE, Preference.WOMEN,
                "Hello, I'm user1", 25, 20, 30, "Warsaw", List.of(Hobby.fromString("Reading")));

        user2 = User.of("user2", "login2", "pass", Sex.FEMALE, Preference.MEN,
                "Hello, I'm user2", 23, 22, 35, "Krakow", List.of(Hobby.fromString("Cooking")));

        user3 = User.of("user3", "login3", "pass", Sex.FEMALE, Preference.WOMEN,
                "Hello, I'm user3", 27, 25, 40, "Gdansk", List.of(Hobby.fromString("Listening to Music")));


        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
    }


    @Test
    void shouldFindAllMatchesForUser() {
        Match match1 = new Match(user1, user2);
        Match match2 = new Match(user2, user3);
        Match match3 = new Match(user1, user3);

        entityManager.persist(match1);
        entityManager.persist(match2);
        entityManager.persist(match3);
        entityManager.flush();

        List<Match> matchesForUser1 = matchRepository.findAllMatchesForUser(user1);
        List<Match> matchesForUser2 = matchRepository.findAllMatchesForUser(user2);
        List<Match> matchesForUser3 = matchRepository.findAllMatchesForUser(user3);

        assertTrue(matchesForUser1.contains(match1));
        assertTrue(matchesForUser1.contains(match3));
        assertFalse(matchesForUser1.contains(match2));

        assertTrue(matchesForUser2.contains(match1));
        assertTrue(matchesForUser2.contains(match2));
        assertFalse(matchesForUser2.contains(match3));

        assertTrue(matchesForUser3.contains(match2));
        assertTrue(matchesForUser3.contains(match3));
        assertFalse(matchesForUser3.contains(match1));
    }
}


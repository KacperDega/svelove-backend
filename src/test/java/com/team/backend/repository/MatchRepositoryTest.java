package com.team.backend.repository;

import com.team.backend.model.City;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Hobby;
import com.team.backend.model.Match;
import com.team.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class MatchRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setup() {
        City warsaw = new City();
        warsaw.setName("Warsaw");
        cityRepository.save(warsaw);

        City krakow = new City();
        krakow.setName("Krakow");
        cityRepository.save(krakow);

        City gdansk = new City();
        gdansk.setName("Gdansk");
        cityRepository.save(gdansk);

        user1 = User.of("user1", "login1", "pass", Sex.MALE, Preference.WOMEN,
                "Hello, I'm user1", 25, 20, 30, warsaw, List.of(new Hobby("reading", "Reading")));
        userRepository.save(user1);

        user2 = User.of("user2", "login2", "pass", Sex.FEMALE, Preference.MEN,
                "Hello, I'm user2", 23, 22, 35, krakow, List.of(new Hobby("cooking", "Cooking")));
        userRepository.save(user2);

        user3 = User.of("user3", "login3", "pass", Sex.FEMALE, Preference.WOMEN,
                "Hello, I'm user3", 27, 25, 40, gdansk, List.of(new Hobby("music", "Music")));
        userRepository.save(user3);
    }

    @Test
    void shouldFindAllMatchesForUser() {
        Match match1 = new Match(user1, user2);
        Match match2 = new Match(user2, user3);
        Match match3 = new Match(user1, user3);

        matchRepository.save(match1);
        matchRepository.save(match2);
        matchRepository.save(match3);

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
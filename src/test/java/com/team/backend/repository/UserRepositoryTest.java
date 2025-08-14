package com.team.backend.repository;

import com.team.backend.model.City;
import com.team.backend.model.Enum.*;
import com.team.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CityRepository cityRepository;

    private User currentUser;
    private User matchingUser;
    private User nonMatchingUser;
    private City lublin;

    @BeforeEach
    void setup() {
        lublin = new City();
        lublin.setName("Lublin");
        cityRepository.save(lublin);

        City krakow = new City();
        krakow.setName("Krakow");
        cityRepository.save(krakow);

        currentUser = new User();
        currentUser.setUsername("marek");
        currentUser.setLogin("maro");
        currentUser.setPassword("pass");
        currentUser.setAge(25);
        currentUser.setCity(lublin);
        currentUser.setSex(Sex.MALE);
        currentUser.setPreference(Preference.BOTH);
        userRepository.save(currentUser);

        matchingUser = new User();
        matchingUser.setUsername("krysia");
        matchingUser.setLogin("krysia");
        matchingUser.setPassword("pass");
        matchingUser.setAge(26);
        matchingUser.setCity(lublin);
        matchingUser.setSex(Sex.FEMALE);
        matchingUser.setPreference(Preference.BOTH);
        userRepository.save(matchingUser);

        nonMatchingUser = new User();
        nonMatchingUser.setUsername("bob");
        nonMatchingUser.setLogin("bob");
        nonMatchingUser.setPassword("pass");
        nonMatchingUser.setAge(35);
        nonMatchingUser.setCity(krakow);
        nonMatchingUser.setSex(Sex.MALE);
        nonMatchingUser.setPreference(Preference.BOTH);
        userRepository.save(nonMatchingUser);
    }

    @Test
    void shouldFindUsersByAgeAndLocationAndExcludeCurrentUser() {
        List<User> results = userRepository.findFilteredByAgeAndLocation(
                currentUser.getId(), 20, 30, lublin.getId());

        assertThat(results)
                .containsExactly(matchingUser)
                .doesNotContain(currentUser, nonMatchingUser);
    }
}




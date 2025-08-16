package com.team.backend.service;

import com.team.backend.model.City;
import com.team.backend.model.Enum.HobbyEnum;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Hobby;
import com.team.backend.model.User;
import com.team.backend.model.dto.PasswordChangeRequest;
import com.team.backend.model.dto.UserProfileUpdateDto;
import com.team.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private EncoderService encoderService;
    @Mock private HobbyService hobbyService;
    @Mock private CityService cityService;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedOldPassword");
    }

    @Test
    void changePassword_shouldChangePasswordWhenOldPasswordIsCorrect() {
        PasswordChangeRequest request = new PasswordChangeRequest("oldPassword", "newPassword");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(encoderService.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(encoderService.encodePassword("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.changePassword("testuser", request);

        assertEquals("encodedNewPassword", result.getPassword());
    }

    @Test
    void changePassword_shouldThrowWhenOldPasswordIsIncorrect() {
        PasswordChangeRequest request = new PasswordChangeRequest("wrongOld", "newPassword");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(encoderService.matches("wrongOld", "encodedOldPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword("testuser", request));
    }

    @Test
    void updateUser_shouldUpdateAllFields() {
        UserProfileUpdateDto dto = new UserProfileUpdateDto(
                "newUsername", "BOTH", List.of(1L),
                "desc", 1L, 18, 20, 30);

        Hobby hobby = new Hobby();
        hobby.setName(HobbyEnum.SWIMMING);
        hobby.setId(1L);
        List<Hobby> hobbies = List.of(hobby);

        City city = new City();
        city.setId(1L);
        city.setName("Warsaw");

        when(hobbyService.getHobbiesByIdList(dto.hobbyIds())).thenReturn(hobbies);
        when(cityService.getCityByIdOrThrow(1L)).thenReturn(city);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User updatedUser = userService.updateUser(user, dto);

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals(Preference.BOTH, updatedUser.getPreference());
        assertEquals(hobbies, updatedUser.getHobbies());
        assertEquals("desc", updatedUser.getDescription());
        assertEquals(1L, updatedUser.getCity().getId());
        assertEquals(18, updatedUser.getAge());
        assertEquals(20, updatedUser.getAge_min());
        assertEquals(30, updatedUser.getAge_max());
    }
}


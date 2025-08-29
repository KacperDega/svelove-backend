package com.team.backend.model.mapper;


import com.team.backend.model.City;
import com.team.backend.model.Hobby;
import com.team.backend.model.User;
import com.team.backend.model.dto.*;
import com.team.backend.repository.HobbyRepository;
import com.team.backend.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserMapper {

    private final HobbyRepository hobbyRepository;
    private final CityService cityService;

    public User mapToUser(RegisterRequest userRequest) {

        City city = cityService.getCityByIdOrThrow(userRequest.cityId());

        List<Hobby> hobbies = userRequest.hobbyIds().stream()
                .map(hobbyId -> hobbyRepository.findById(hobbyId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid hobby with Id: " + hobbyId)))
                .collect(Collectors.toList());
        log.debug(hobbies.toString());

        return User.of(
                userRequest.username(),
                userRequest.login(),
                userRequest.password(),
                userRequest.sex(),
                userRequest.preference(),
                userRequest.description(),
                userRequest.age(),
                userRequest.ageMin(),
                userRequest.ageMax(),
                city,
                hobbies
        );
    }

    public RegisterResponseDto mapToRegisterResponse(User user) {
        // String responseMessage = "REGISTERED";
        return new RegisterResponseDto(user.getUsername(), user.getLogin(), "REGISTERED");
    }

    public LoginResponseDto mapToUserResponse(User user) {
        return new LoginResponseDto(user.getUsername(), user.getLogin(), user.getPassword());
    }

    public static UserMatchDto mapToUserMatchDto(User user) {
        return new UserMatchDto(
                user.getId(),
                user.getUsername(),
                user.getSex(),
                user.getAge(),
                user.getCity().getName(),
                user.getPreference(),
                user.getHobbies().stream()
                        .map(Hobby::getName)
                        .toList(),
                user.getDescription(),
                user.getPhotoUrls()
        );
    }


    public static UserProfileDto mapToUserProfileDto(User user) {
        return new UserProfileDto(
                user.getUsername(),
                user.getLogin(),
                user.getSex().getDisplayName(),
                user.getPreference().getDisplayName(),
                user.getHobbies().stream()
                        .map(Hobby::getName)
                        .toList(),
                user.getDescription(),
                user.getCity().getName(),
                user.getPhotoUrls(),
                user.getAge(),
                user.getAge_min(),
                user.getAge_max()
        );
    }
}
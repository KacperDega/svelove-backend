package com.team.backend.model.mapper;


import com.team.backend.model.Enum.HobbyEnum;
import com.team.backend.model.Hobby;
import com.team.backend.model.User;
import com.team.backend.model.dto.*;
import com.team.backend.repository.HobbyRepository;
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

    public User mapToUser(RegisterRequest userRequest) {

        List<Hobby> hobbies = userRequest.hobbies().stream()
                .map(hobbyEntity -> {
                    HobbyEnum enumHobby = hobbyEntity.getName();
                    List<Hobby> existing = hobbyRepository.findByName(enumHobby);
                    if (!existing.isEmpty()) {
                        return existing.get(0);
                    } else {
                        Hobby newHobby = new Hobby();
                        newHobby.setName(enumHobby);
                        return hobbyRepository.save(newHobby);
                    }
                })
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
                userRequest.age_min(),
                userRequest.age_max(),
                userRequest.localization(),
                hobbies
        );
    }

    public RegisterResponseDto mapToRegisterResponse(User user) {
        String responseMessage = "REGISTERED";
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
                user.getLocalization(),
                user.getPreference(),
                user.getHobbies().stream()
                                .map(Hobby::getHobbyName)
                                .toList(),
                user.getDescription()
        );
    }



    public static UserProfileDto mapToUserProfileDto(User user) {
        return new UserProfileDto(
                user.getUsername(),
                user.getLogin(),
                user.getSex().getDisplayName(),
                user.getPreference().getDisplayName(),
                user.getHobbies().stream().map(Hobby::getHobbyName).toList(),
                user.getDescription(),
                user.getLocalization(),
                user.getAge(),
                user.getAge_min(),
                user.getAge_max()
        );
    }
}
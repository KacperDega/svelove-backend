package com.team.backend.model.dto;

import java.util.List;

public record UserProfileDto(
        String username,
        String login,
        String sex,
        String preference,
        List<String> hobbies,
        String description,
        String localization,
        List<String> photoUrls,
        int age,
        int age_min,
        int age_max
) {
}

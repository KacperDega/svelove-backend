package com.team.backend.model.dto;

import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;

import java.util.List;

public record UserMatchDto(
        Long id,
        String username,
        Sex sex,
        int age,
        String localization,
        Preference preference,
        List<String> hobbies,
        String description,
        List<String> photoUrls
) {}

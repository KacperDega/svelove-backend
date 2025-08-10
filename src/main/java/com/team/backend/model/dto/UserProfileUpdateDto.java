package com.team.backend.model.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record UserProfileUpdateDto(

        @NotBlank
        @Size(min = 3, max = 30)
        String username,

        @NotBlank
        String preference,

        @NotEmpty
        List<Long> hobbyIds,

        @Size(max = 255)
        String description,

        @NotBlank
        String localization,

        @NotNull
        @Min(18)
        @Max(100)
        Integer age,

        @NotNull
        @Min(18)
        @Max(100)
        Integer ageMin,

        @NotNull
        @Min(18)
        @Max(100)
        Integer ageMax
) {
}

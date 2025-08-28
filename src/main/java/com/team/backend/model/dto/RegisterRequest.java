package com.team.backend.model.dto;

import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import jakarta.validation.constraints.*;

import java.util.List;

public record RegisterRequest(

        @NotNull(message = "{username.not.null}")
        @NotEmpty(message = "{username.not.empty}")
        @NotBlank(message = "{username.not.blank}")
        String username,

        @NotNull(message = "{login.not.null}")
        @NotEmpty(message = "{login.not.empty}")
        @NotBlank(message = "{login.not.blank}")
        String login,

        @NotNull(message = "{password.not.null}")
        @NotEmpty(message = "{password.not.empty}")
        @NotBlank(message = "{password.not.blank}")
        String password,

        Sex sex,

        Preference preference,

        @NotNull(message = "{description.not.null}")
        @NotEmpty(message = "{description.not.empty}")
        @NotBlank(message = "{description.not.blank}")
        String description,

        @NotNull(message = "{age.not.null}")
        @Min(18)
        @Max(100)
        Integer age,

        @NotNull(message = "{age.not.null}")
        @Min(18)
        @Max(100)
        Integer ageMin,

        @NotNull(message = "{age.not.null}")
        @Min(18)
        @Max(100)
        Integer ageMax,

        @NotNull(message = "{city_id.not.null}")
        Long cityId,

        @NotNull(message = "{hobby_list.not.null}")
        @NotEmpty(message = "{hobby_list.not.empty}")
        List<Long> hobbyIds

) {
}

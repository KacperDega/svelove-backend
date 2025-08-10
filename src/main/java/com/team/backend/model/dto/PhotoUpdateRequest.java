package com.team.backend.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PhotoUpdateRequest(

        @NotNull(message = "Photo list cannot be null")
        @Size(min = 1, max = 5, message = "Photo list must contain between 1 and 5 elements")
        List<String> orderedPhotoUrls
) {
}

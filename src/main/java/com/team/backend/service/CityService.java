package com.team.backend.service;

import com.team.backend.model.City;
import com.team.backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public City getCityByIdOrThrow(Long cityId) {
        return cityRepository.findCityById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("City not found with id: " + cityId));
    }
}

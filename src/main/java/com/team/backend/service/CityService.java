package com.team.backend.service;

import com.team.backend.model.City;
import com.team.backend.model.dto.CityDto;
import com.team.backend.model.mapper.CityMapper;
import com.team.backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<CityDto> getAllCities() {
        List<City> cities = cityRepository.findAll();
        return CityMapper.mapToCityDtoList(cities);
    }

    public City getCityByIdOrThrow(Long cityId) {
        return cityRepository.findCityById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("City not found with id: " + cityId));
    }
}

package com.team.backend.model.mapper;

import com.team.backend.model.City;
import com.team.backend.model.dto.CityDto;

import java.util.List;
import java.util.stream.Collectors;

public class CityMapper {

    public static CityDto mapToCityDto(City city) {
        return new CityDto(
                city.getId(),
                city.getName()
        );
    }

    public static List<CityDto> mapToCityDtoList(List<City> cities) {
        return cities.stream()
                .map(CityMapper::mapToCityDto)
                .collect(Collectors.toList());
    }
}


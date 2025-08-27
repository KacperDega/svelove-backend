package com.team.backend.controller;

import com.team.backend.model.dto.CityDto;
import com.team.backend.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cities")
public class CityController {

    private final CityService cityService;

    @GetMapping()
    public ResponseEntity<List<CityDto>> getCities() {
        return ResponseEntity.ok(cityService.getAllCitiesSorted());
    }
}

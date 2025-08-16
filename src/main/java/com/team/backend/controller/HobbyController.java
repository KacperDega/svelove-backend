package com.team.backend.controller;

import com.team.backend.model.Hobby;
import com.team.backend.service.HobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hobbies")
public class HobbyController {

    private final HobbyService hobbyService;

    @GetMapping
    public ResponseEntity<List<Hobby>> getAllHobbies() {
        return ResponseEntity.ok(hobbyService.getAllHobbies());
    }
}

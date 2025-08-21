package com.team.backend.controller;

import com.team.backend.model.User;
import com.team.backend.model.UserStatsMonthly;
import com.team.backend.model.dto.AvailableStatsDto;
import com.team.backend.model.dto.UserStatsDto;
import com.team.backend.model.mapper.UserStatsMapper;
import com.team.backend.service.UserService;
import com.team.backend.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profile/stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserService userService;
    private final UserStatsService statsService;

    @GetMapping("/available")
    public ResponseEntity<List<AvailableStatsDto>> getAvailableStats(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);

        List<AvailableStatsDto> available = statsService.getAvailableYearMonthsForStats(currentUser)
                .stream()
                .map(UserStatsMapper::toAvailableStatsDto)
                .toList();

        return ResponseEntity.ok(available);
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<UserStatsDto> getStatsForMonth(
            Authentication authentication,
            @PathVariable int year,
            @PathVariable int month
    ) {
        User currentUser = userService.getCurrentUser(authentication);
        UserStatsMonthly stats = statsService.getStatsForMonth(currentUser, year, month);
        return ResponseEntity.ok(UserStatsMapper.toUserStatsDto(stats));
    }

    @GetMapping("/{year}")
    public ResponseEntity<UserStatsDto> getStatsForYear(@PathVariable int year, Authentication authentication)
    {
        User currentUser = userService.getCurrentUser(authentication);
        UserStatsMonthly stats = statsService.getStatsForYear(currentUser, year);
        return ResponseEntity.ok(UserStatsMapper.toUserStatsDto(stats));
    }

    @GetMapping("/lifetime")
    public ResponseEntity<UserStatsDto> getLifetimeStats(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        UserStatsMonthly stats = statsService.getLifetimeStats(currentUser);
        return ResponseEntity.ok(UserStatsMapper.toUserStatsDto(stats));
    }
}


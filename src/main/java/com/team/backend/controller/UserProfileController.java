package com.team.backend.controller;

import com.team.backend.model.User;
import com.team.backend.model.dto.PasswordChangeRequest;
import com.team.backend.model.dto.UserProfileDto;
import com.team.backend.model.dto.UserProfileUpdateDto;
import com.team.backend.model.mapper.UserMapper;
import com.team.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfileDetails(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        UserProfileDto userProfileDto = UserMapper.mapToUserProfileDto(currentUser);

        return ResponseEntity.ok(userProfileDto);
    }

    @PatchMapping("/edit")
    public ResponseEntity<?> editProfile(@RequestBody UserProfileUpdateDto updateDto,
                                         Authentication authentication,
                                         BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        String login = authentication.getName();
        User currentUser = userService.getUserByLogin(login);

        userService.updateUser(currentUser, updateDto);
        return ResponseEntity.ok("Profile updated");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeRequest request, Authentication authentication) {
        String login = authentication.getName();

        userService.changePassword(login, request);

        return ResponseEntity.ok("Password changed successfully");
    }

}


package com.team.backend.controller;

import com.team.backend.model.User;
import com.team.backend.model.dto.PhotoUpdateRequest;
import com.team.backend.service.PhotoService;
import com.team.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile/photos")
@RequiredArgsConstructor
public class UserPhotosController {

    private final UserService userService;
    private final PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file,
                                         Authentication auth) throws IOException {
        String login = auth.getName();
        User user = userService.getUserByLogin(login);

        try {
            String url = photoService.uploadUserPhoto(user, file);
            return ResponseEntity.ok(url);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deletePhoto(@RequestParam("url") String url, Authentication auth) {
        String login = auth.getName();
        User user = userService.getUserByLogin(login);

        try {
            photoService.deleteUserPhoto(user, url);
            return ResponseEntity.ok("Photo deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/order")
    public ResponseEntity<?> updatePhotoOrder(@RequestBody @Valid PhotoUpdateRequest request,
                                              Authentication auth) {
        String login = auth.getName();
        User user = userService.getUserByLogin(login);

        try {
            photoService.updatePhotoOrder(user, request.orderedPhotoUrls());
            return ResponseEntity.ok("Photo order updated");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}


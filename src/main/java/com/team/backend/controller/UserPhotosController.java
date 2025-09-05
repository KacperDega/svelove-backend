package com.team.backend.controller;

import com.team.backend.model.User;
import com.team.backend.service.PhotoService;
import com.team.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @PutMapping("/profile/photos")
    public ResponseEntity<?> updatePhotos(
            @RequestPart("orderedPhotoUrls") List<String> orderedPhotoUrls,
            @RequestPart(value = "newPhotos", required = false) List<MultipartFile> newPhotos,
            Authentication auth
    ) {
        String login = auth.getName();
        User user = userService.getUserByLogin(login);

        try {
            List<String> finalUrls = photoService.updatePhotos(user, orderedPhotoUrls, newPhotos);
            return ResponseEntity.ok(finalUrls);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }


}


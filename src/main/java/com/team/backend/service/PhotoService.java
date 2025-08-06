package com.team.backend.service;

import com.team.backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PhotoService {

    private final UserService userService;
    private final SupabaseStorageService storageService;

    public String uploadUserPhoto(User user, MultipartFile file) throws IOException {
        if (user.getPhotoUrls().stream().filter(Objects::nonNull).count() >= 5) {
            throw new IllegalStateException("Max 5 photos per user allowed.");
        }

        String url = storageService.uploadImage(file, user.getId());

        List<String> photos = new ArrayList<>(user.getPhotoUrls());
        int freeIndex = photos.indexOf(null);

        if (freeIndex == -1) {
            freeIndex = photos.size();
        }

        if (freeIndex < 5) {
            if (freeIndex == photos.size()) {
                photos.add(url);
            } else {
                photos.set(freeIndex, url);
            }
            user.setPhotoUrls(photos);
            userService.saveUser(user);
        } else {
            // rollback
            storageService.deleteImage(url);
            throw new IllegalStateException("No free slot for photo");
        }

        return url;
    }


    public void deleteUserPhoto(User user, String url) {
        List<String> photos = new ArrayList<>(user.getPhotoUrls());

        if (!photos.contains(url)) {
            throw new IllegalArgumentException("Photo URL not found");
        }

        storageService.deleteImage(url);

        int idx = photos.indexOf(url);
        photos.set(idx, null);

        user.setPhotoUrls(photos);
        userService.saveUser(user);
    }


    public void updatePhotoOrder(User user, List<String> newOrder) {
        if (newOrder == null || newOrder.isEmpty() || newOrder.stream().noneMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("At least one photo must be provided");
        }

        if (newOrder.size() > 5) {
            throw new IllegalArgumentException("Maximum of 5 photos allowed");
        }

        for (String url : newOrder) {
            if (url != null && !user.getPhotoUrls().contains(url)) {
                throw new IllegalArgumentException("Invalid photo URL in order list");
            }
        }

        long nonNullCount = newOrder.stream().filter(Objects::nonNull).count();
        long distinctNonNullCount = newOrder.stream().filter(Objects::nonNull).distinct().count();

        if (nonNullCount != distinctNonNullCount) {
            throw new IllegalArgumentException("Duplicate photo URLs are not allowed");
        }

        List<String> finalOrder = new ArrayList<>(newOrder);
        while (finalOrder.size() < 5) {
            finalOrder.add(null);
        }

        user.setPhotoUrls(finalOrder);
        userService.saveUser(user);
    }

}

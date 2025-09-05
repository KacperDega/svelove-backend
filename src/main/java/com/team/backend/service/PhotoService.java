package com.team.backend.service;

import com.team.backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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


    public List<String> updatePhotos(User user, List<String> orderedPhotoUrls, List<MultipartFile> newPhotos) throws IOException {
        if (orderedPhotoUrls == null || orderedPhotoUrls.isEmpty() || orderedPhotoUrls.stream().noneMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("At least one photo must be provided");
        }

        if (orderedPhotoUrls.size() > 5) {
            throw new IllegalArgumentException("Maximum of 5 photos allowed");
        }

        orderedPhotoUrls = orderedPhotoUrls.stream()
                .map(s -> s != null && s.isEmpty() ? null : s)
                .collect(Collectors.toList());

        for (String url : orderedPhotoUrls) {
            if (url != null && !user.getPhotoUrls().contains(url)) {
                throw new IllegalArgumentException("Invalid photo URL in order list");
            }
        }

        long nonNullCount = orderedPhotoUrls.stream().filter(Objects::nonNull).count();
        long distinctNonNullCount = orderedPhotoUrls.stream().filter(Objects::nonNull).distinct().count();
        if (nonNullCount != distinctNonNullCount) {
            throw new IllegalArgumentException("Duplicate photo URLs are not allowed");
        }

        List<String> finalOrder = new ArrayList<>();
        List<String> uploadedUrls = new ArrayList<>();
        int uploadIndex = 0;

        try {
            for (String url : orderedPhotoUrls) {
                if (url == null) {
                    if (newPhotos != null && uploadIndex < newPhotos.size()) {
                        String newUrl = storageService.uploadImage(newPhotos.get(uploadIndex++), user.getId());
                        uploadedUrls.add(newUrl); // rollback
                        finalOrder.add(newUrl);
                    } else {
                        finalOrder.add(null);
                    }
                } else {
                    finalOrder.add(url);
                }
            }
        } catch (Exception ex) {
            // upload rollback
            for (String u : uploadedUrls) {
                try {
                    storageService.deleteImage(u);
                } catch (Exception ignore) {}
            }
            throw new IOException("Photo upload failed: " + ex.getMessage(), ex);
        }

        for (String existingUrl : user.getPhotoUrls()) {
            if (existingUrl != null && !finalOrder.contains(existingUrl)) {
                try {
                    storageService.deleteImage(existingUrl);
                } catch (Exception ex) {
                    System.err.println("Nie udało się usunąć starego zdjęcia: " + existingUrl);
                }
            }
        }

        while (finalOrder.size() < 5) {
            finalOrder.add(null);
        }

        user.setPhotoUrls(finalOrder);
        userService.saveUser(user);

        return finalOrder;
    }

}

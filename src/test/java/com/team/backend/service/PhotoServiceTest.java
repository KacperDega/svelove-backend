package com.team.backend.service;

import com.team.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    private UserService userService;
    private SupabaseStorageService storageService;
    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        storageService = mock(SupabaseStorageService.class);
        photoService = new PhotoService(userService, storageService);
    }

    // ========================================
    // uploadUserPhoto()
    // ========================================
    @Test
    void shouldUploadPhotoWhenLessThan5Photos() throws IOException {
        User user = new User();
        user.setId(123L);
        user.setPhotoUrls(new ArrayList<>(Arrays.asList("url1", null, "url3")));

        MultipartFile file = mock(MultipartFile.class);
        when(storageService.uploadImage(file, 123L)).thenReturn("newUrl");

        String result = photoService.uploadUserPhoto(user, file);

        assertEquals("newUrl", result);
        assertTrue(user.getPhotoUrls().contains("newUrl"));
        verify(userService).saveUser(user);
    }

    @Test
    void shouldThrowWhenUserHas5Photos() {
        User user = new User();
        user.setPhotoUrls(Arrays.asList("1", "2", "3", "4", "5"));

        MultipartFile file = mock(MultipartFile.class);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            photoService.uploadUserPhoto(user, file);
        });

        assertEquals("Max 5 photos per user allowed.", exception.getMessage());
    }

    // ========================================
    // deleteUserPhoto()
    // ========================================

    @Test
    void shouldDeletePhoto() {
        User user = new User();
        user.setPhotoUrls(new ArrayList<>(Arrays.asList("a", "b", "c")));

        photoService.deleteUserPhoto(user, "b");

        assertEquals(Arrays.asList("a", null, "c"), user.getPhotoUrls());
        verify(storageService).deleteImage("b");
        verify(userService).saveUser(user);
    }

    @Test
    void shouldThrowWhenDeletingPhotoThatDoesNotExist() {
        User user = new User();
        user.setPhotoUrls(new ArrayList<>(Arrays.asList("a", "b", "c")));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            photoService.deleteUserPhoto(user, "x");
        });

        assertEquals("Photo URL not found", exception.getMessage());
    }

    // ========================================
    // updatePhotoOrder()
    // ========================================

    @Test
    void shouldUpdatePhotoOrderCorrectly() {
        User user = new User();
        user.setPhotoUrls(Arrays.asList("1", "2", "3", null, null));

        List<String> newOrder = Arrays.asList("3", "1", null);

        photoService.updatePhotoOrder(user, newOrder);

        List<String> expected = Arrays.asList("3", "1", null, null, null);
        assertEquals(expected, user.getPhotoUrls());
        verify(userService).saveUser(user);
    }

    @Test
    void shouldThrowWhenDuplicateUrlsInOrder() {
        User user = new User();
        user.setPhotoUrls(Arrays.asList("1", "2", "3", null, null));

        List<String> newOrder = Arrays.asList("2", "2", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            photoService.updatePhotoOrder(user, newOrder);
        });

        assertEquals("Duplicate photo URLs are not allowed", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUnknownUrlInNewOrder() {
        User user = new User();
        user.setPhotoUrls(Arrays.asList("1", "2", null, null, null));

        List<String> newOrder = Arrays.asList("1", "x", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            photoService.updatePhotoOrder(user, newOrder);
        });

        assertEquals("Invalid photo URL in order list", exception.getMessage());
    }

    @Test
    void shouldThrowWhenOrderIsEmpty() {
        User user = new User();
        user.setPhotoUrls(Arrays.asList("1", "2", null, null, null));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            photoService.updatePhotoOrder(user, new ArrayList<>());
        });

        assertEquals("At least one photo must be provided", exception.getMessage());
    }

}

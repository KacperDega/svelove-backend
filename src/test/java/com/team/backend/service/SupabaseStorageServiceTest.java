package com.team.backend.service;

import com.team.backend.client.SupabaseStorageClient;
import com.team.backend.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupabaseStorageServiceTest {

    @Mock private UserService userService;
    @Mock private SupabaseStorageClient supabaseStorageClient;
    @Mock private EncoderService encoderService;

    private SupabaseStorageService service;

    @BeforeEach
    void setUp() {
        service = new SupabaseStorageService(userService, supabaseStorageClient, encoderService);
        ReflectionTestUtils.setField(service, "bucket", "bucket");
        ReflectionTestUtils.setField(service, "baseUrl", "http://base.url");
    }

    private MultipartFile create1x1PngFile() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile("file", "image.png", "image/png", imageBytes);
    }

    // ========================================
    // uploadImage() - HAPPY PATH
    // ========================================
    @Test
    void uploadImage_shouldReturnUrl_whenAllIsFine() throws IOException {
        Long userId = 1L;

        // ImageIO.read(...) needs real image so create simple 1x1
        MultipartFile file = create1x1PngFile();

        User user = mock(User.class);
        when(user.getPhotoUrls()).thenReturn(List.of());
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(encoderService.encodeUserIdToFolderHash(userId)).thenReturn("userhash");
        when(supabaseStorageClient.createPublicUrl(any())).thenReturn("http://base.url/userhash/file.webp");

        String url = service.uploadImage(file, userId);

        assertTrue(url.contains("http://base.url/userhash/"));
    }


    // ========================================
    // uploadImage() - FILE VALIDATION
    // ========================================
    @Test
    void uploadImage_shouldThrow_whenFileIsEmpty() {
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);

        when(userService.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertEquals("File must not be empty.", ex.getMessage());
    }

    @Test
    void uploadImage_shouldThrow_whenMimeTypeIsWrong() {
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "application/pdf", "abc".getBytes());

        when(userService.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertTrue(ex.getMessage().contains("Unsupported image content type"));
    }

    @Test
    void uploadImage_shouldThrow_whenExtensionIsWrong() {
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "image.txt", "image/jpeg", "abc".getBytes());

        when(userService.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertTrue(ex.getMessage().contains("Unsupported image file extension"));
    }

    @Test
    void uploadImage_shouldNotThrow_whenImageIsCorrect() throws Exception {
        Long userId = 1L;

        // ImageIO.read(...) needs real image so create simple 1x1
        MultipartFile file = create1x1PngFile();

        User user = mock(User.class);
        when(user.getPhotoUrls()).thenReturn(List.of());
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(encoderService.encodeUserIdToFolderHash(userId)).thenReturn("userhash");
        when(supabaseStorageClient.createPublicUrl(any())).thenReturn("http://base.url/userhash/file.webp");

        assertDoesNotThrow(() -> service.uploadImage(file, userId));
    }


    // ========================================
    // uploadImage() - CHECK USER PHOTO LIMIT
    // ========================================
    @Test
    void uploadImage_shouldThrow_whenUserNotFound() {
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "abc".getBytes());

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertTrue(ex.getMessage().contains("User with id 1 not found."));
    }

    @Test
    void uploadImage_shouldThrow_whenUserHasTooManyPhotos() {
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "abc".getBytes());

        User user = mock(User.class);
        when(user.getPhotoUrls()).thenReturn(List.of("1", "2", "3", "4", "5"));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(IllegalStateException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertTrue(ex.getMessage().contains("Max 5 photos per user allowed."));
    }

    @Test
    void uploadImage_shouldNotThrow_whenUserHasLessThan5Photos() throws IOException {
        Long userId = 1L;

        // ImageIO.read(...) needs real image so create simple 1x1
        MultipartFile file = create1x1PngFile();

        User user = mock(User.class);
        when(user.getPhotoUrls()).thenReturn(List.of("1", "2"));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(encoderService.encodeUserIdToFolderHash(userId)).thenReturn("userhash");
        when(supabaseStorageClient.createPublicUrl(any())).thenReturn("http://base.url/userhash/file.webp");

        assertDoesNotThrow(() -> service.uploadImage(file, userId));
    }


    // ========================================
    // uploadImage() - EXTENSION EXTRACTION
    // ========================================
    @Test
    void uploadImage_shouldThrow_whenFilenameHasNoExtension() {
        Long userId = 1L;
        MultipartFile file = new MockMultipartFile("file", "file", "image/jpeg", "abc".getBytes());

        when(userService.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertTrue(ex.getMessage().contains("Unsupported image file extension"));
    }

    @Test
    void uploadImage_shouldThrow_whenFilenameIsNull() throws IOException {
        Long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.isEmpty()).thenReturn(false);

        when(userService.getUserById(userId)).thenReturn(Optional.of(mock(User.class)));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            service.uploadImage(file, userId);
        });

        assertTrue(ex.getMessage().contains("Unsupported image file extension"));
    }
}

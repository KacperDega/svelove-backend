package com.team.backend.service;

import com.team.backend.client.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.url}")
    private String baseUrl;

    private final SupabaseStorageClient supabaseStorageClient;
    private final EncoderService encoderService;

    public String uploadImage(MultipartFile file, Long userId) throws IOException {
        validateFile(file);

        if (userId == null) {
            throw new IllegalArgumentException("Parameter userId cannot be null");
        }

        String contentType = file.getContentType();
        String extension = extractExtension(file.getOriginalFilename());

        String uniqueFileName = UUID.randomUUID() + extension;
        String userHash = encoderService.encodeUserIdToFolderHash(userId);
        String filePath = String.format("%s/%s", userHash, uniqueFileName);

        supabaseStorageClient.uploadFile(file.getBytes(), contentType, filePath);

        return supabaseStorageClient.createPublicUrl(filePath);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isSupportedImageContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported image content type: " + contentType);
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!isSupportedImageExtension(extension)) {
            throw new IllegalArgumentException("Unsupported image file extension: " + extension);
        }
    }

    private String extractExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.')))
                .orElse("");
    }

    private boolean isSupportedImageContentType(String contentType) {
        return List.of("image/jpeg", "image/png", "image/webp").contains(contentType.toLowerCase());
    }

    private boolean isSupportedImageExtension(String extension) {
        return List.of(".jpg", ".jpeg", ".png", ".webp").contains(extension.toLowerCase());
    }

    public void deleteImage(String publicUrl) {
        String filePath = supabaseStorageClient.extractFilePathFromPublicUrl(publicUrl);

        supabaseStorageClient.deleteFile(bucket, filePath);
    }
}

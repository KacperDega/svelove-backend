package com.team.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

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

    private final WebClient supabaseWebClient;

    private final EncoderService encoderService;

    public String uploadImage(MultipartFile file, Long userId) throws IOException {
        if (userId == null) {
            throw new IllegalArgumentException("Parameter userId cannot be null");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isSupportedImageContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported image content type: " + contentType);
        }

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf('.')))
                .orElse("");

        if (!isSupportedImageExtension(extension)) {
            throw new IllegalArgumentException("Unsupported image file extension: " + extension);
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        String userHash = encoderService.encodeUserIdToFolderHash(userId);
        String filePath = String.format("%s/%s", userHash, uniqueFileName);
        String path = String.format("/storage/v1/object/%s/%s", bucket, filePath);

        supabaseWebClient.put()
                .uri(path)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .bodyValue(file.getBytes())
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Upload failed: " + body)))
                .bodyToMono(Void.class)
                .block();

        return String.format("%s/storage/v1/object/public/%s/%s", baseUrl, bucket, filePath);
    }

    private boolean isSupportedImageContentType(String contentType) {
        return List.of("image/jpeg", "image/png", "image/webp").contains(contentType.toLowerCase());
    }

    private boolean isSupportedImageExtension(String extension) {
        return List.of(".jpg", ".jpeg", ".png", ".webp").contains(extension.toLowerCase());
    }


}

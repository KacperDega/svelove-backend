package com.team.backend.service;

import com.team.backend.client.SupabaseStorageClient;
import com.team.backend.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final UserService userService;
    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.url}")
    private String baseUrl;

    private final SupabaseStorageClient supabaseStorageClient;
    private final EncoderService encoderService;

    public String uploadImage(MultipartFile file, Long userId) throws IOException {
        if (userId == null) {
            throw new IllegalArgumentException("Parameter userId cannot be null.");
        }

        checkPhotoLimit(userId);
        validateFile(file);

        byte[] webpBytes = convertToWebPWithoutMetadata(file);
        String uniqueFileName = UUID.randomUUID() + ".webp";

        String userHash = encoderService.encodeUserIdToFolderHash(userId);
        String filePath = String.format("%s/%s", userHash, uniqueFileName);

        supabaseStorageClient.uploadFile(webpBytes, "image/webp", filePath);

        return supabaseStorageClient.createPublicUrl(filePath);
    }


    private void checkPhotoLimit(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found."));

        if (user.getPhotoUrls().size() >= 5) {
            throw new IllegalStateException("Max 5 photos per user allowed.");
        }
    }


    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
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

    private byte[] convertToWebPWithoutMetadata(MultipartFile file) throws IOException {
        ImageIO.scanForPlugins();

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new IllegalArgumentException("Invalid image file");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(param.getCompressionTypes()[0]);
            param.setCompressionQuality(1.0f);

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }

        return baos.toByteArray();
    }


    public void deleteImage(String publicUrl) {
        String filePath = supabaseStorageClient.extractFilePathFromPublicUrl(publicUrl);

        supabaseStorageClient.deleteFile(filePath);
    }
}

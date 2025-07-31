package com.team.backend.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SupabaseStorageClient {

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.url}")
    private String baseUrl;

    private final WebClient supabaseWebClient;

    public void uploadFile(byte[] fileBytes, String contentType, String filePath) {
        String path = String.format("/storage/v1/object/%s/%s", bucket, filePath);

        supabaseWebClient.put()
                .uri(path)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .bodyValue(fileBytes)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Upload failed: " + body)))
                .bodyToMono(Void.class)
                .block();
    }

    public String createPublicUrl(String filePath) {
        return String.format("%s/storage/v1/object/public/%s/%s", baseUrl, bucket, filePath);
    }

    public void deleteFile(String bucket, String filePath) {
        String deletePath = String.format("/storage/v1/object/%s/%s", bucket, filePath);

        supabaseWebClient.delete()
                .uri(deletePath)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Delete failed: " + body)))
                .bodyToMono(Void.class)
                .block();
    }

    public String extractFilePathFromPublicUrl(String publicUrl) {
        String pathPrefix = "/public/" + bucket + "/";
        int index = publicUrl.indexOf(pathPrefix);

        if (index == -1) {
            throw new IllegalArgumentException("Invalid public URL: cannot extract file path.");
        }

        return publicUrl.substring(index + pathPrefix.length());
    }

}


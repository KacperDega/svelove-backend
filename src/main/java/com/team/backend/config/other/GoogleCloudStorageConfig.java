//package com.team.backend.config.other;
//
//import com.google.api.gax.core.CredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.spring.core.GcpProjectIdProvider;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import lombok.AllArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//
//@Log4j2
//@Configuration
//@AllArgsConstructor
//public class GoogleCloudStorageConfig {
//
//    private final GcpProjectIdProvider projectIdProvider;
//    private final CredentialsProvider credentialsProvider;
//
//    @Bean
//    public Storage storage() throws IOException {
//        String projectId = projectIdProvider.getProjectId();
//        GoogleCredentials credentials = (GoogleCredentials) credentialsProvider.getCredentials();
//
//        StorageOptions storageOptions = StorageOptions.newBuilder()
//                .setProjectId(projectId)
//                .setCredentials(credentials)
//                .build();
//
//        log.info("Google Cloud Storage initialized with project ID: {}", storageOptions.getProjectId());
//
//        return storageOptions.getService();
//    }
//}

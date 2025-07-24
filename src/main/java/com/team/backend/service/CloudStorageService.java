//package com.team.backend.service;
//
//import com.google.api.gax.paging.Page;
//import com.google.auth.Credentials;
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Log4j2
//@Service
//public class CloudStorageService {
//
//    private final Storage storage;
//    private final String bucketName;
//    private final String mediaFolder;
//
//    public CloudStorageService(Storage storage,
//                               @Value("${gcp.bucket.name}") String bucketName,
//                               @Value("${gcp.storage.media-folder}") String mediaFolder) {
//        this.storage = storage;
//        this.bucketName = bucketName;
//        this.mediaFolder = mediaFolder;
//    }
//
//    public String uploadFile(MultipartFile file, String user, String contentType) throws IOException {
//        String filename = user + "/" + UUID.randomUUID().toString() +
//                getExtension(file.getOriginalFilename());
//        String objectName = mediaFolder + "/" + filename;
//
//        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
//                .setContentType(contentType)
//                .build();
//
//        storage.create(blobInfo, file.getBytes());
//
//        // Generate a signed URL that expires in 1 hour
//        URL signedUrl = storage.signUrl(blobInfo, 60, TimeUnit.MINUTES,
//                Storage.SignUrlOption.withV4Signature());
//
//        return objectName;
//    }
//
//    public List<String> getUserMediaUrls(String user) {
//        String prefix = mediaFolder + "/" + user + "/";
//        log.debug("User link = {}/{}/", mediaFolder, user);
//        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(prefix));
//
//        List<String> mediaUrls = new ArrayList<>();
//        for (Blob blob : blobs.iterateAll()) {
//            // Generate a signed URL for each file
//            URL signedUrl = storage.signUrl(BlobInfo.newBuilder(bucketName, blob.getName()).build(),
//                    60, TimeUnit.MINUTES,
//                    Storage.SignUrlOption.withV4Signature());
//            mediaUrls.add(signedUrl.toString());
//        }
//
//        return mediaUrls;
//    }
//
//    public Credentials checkStorage(){
//        return storage.getOptions().getCredentials();
//    }
//
//    public void deleteFile(String objectName) {
//        storage.delete(bucketName, objectName);
//    }
//
//    private String getExtension(String filename) {
//        return filename.substring(filename.lastIndexOf("."));
//    }
//}
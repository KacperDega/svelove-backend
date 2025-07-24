//package com.team.backend.controller;
//
//import com.team.backend.service.CloudStorageService;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@Log4j2
//@RequestMapping("/api/media")
//public class MediaController {
//
//    private final CloudStorageService storageService;
//
//    public MediaController(CloudStorageService storageService) {
//        this.storageService = storageService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, String>> uploadMedia(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("type") String type,
//            Authentication authentication){
//        log.debug("Credentials when upload : {}",storageService.checkStorage() );
//
//        log.debug("UPLOAD REQUEST INTIATED");
//        log.debug("Upload request received. File name: {}, Type: {}, Content-Type: {}",
//                file.getOriginalFilename(), type, file.getContentType());
//        log.debug("Request Content-Type: {}", file.getContentType());
//        log.debug("File size: {}", file.getSize());
//
//        try {
//            String user = (String) authentication.getPrincipal();
//            if (!isValidFileType(file, type)) {
//                return ResponseEntity.badRequest().body(
//                        Collections.singletonMap("error", "Invalid file type"));
//            }
//
//            String objectName = storageService.uploadFile(file, user, file.getContentType());
//
//            Map<String, String> response = new HashMap<>();
//            response.put("objectName", objectName);
//            response.put("message", "File uploaded successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (IOException e) {
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Failed to upload file: " + e.getMessage()));
//        }
//    }
//
//    @GetMapping("/list")
//    public ResponseEntity<List<String>> getUserMedia(Authentication authentication) {
//        String user = (String) authentication.getPrincipal();
//        log.debug("Fetching media for user: {}", user);
//        List<String> mediaUrls = storageService.getUserMediaUrls(user);
//        log.debug("Media URLs fetched: {}", mediaUrls);
//
//        log.debug("Credentials when list : {}, {}", user,storageService.checkStorage() );
//
//        return ResponseEntity.ok(mediaUrls);
//    }
//
//    @DeleteMapping("/{objectName}")
//    public ResponseEntity<Map<String, String>> deleteMedia(
//            @PathVariable String objectName,
//            Authentication authentication) {
//
//        String user = (String) authentication.getPrincipal();
//        log.debug("Delete request for object: {} by user: {}", objectName, user);
//        // Security check: ensure the file belongs to the authenticated user
//        if (!objectName.contains("/" + user + "/")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Collections.singletonMap("error", "You cannot delete this file"));
//        }
//
//        storageService.deleteFile(objectName);
//
//        return ResponseEntity.ok(Collections.singletonMap("message", "File deleted successfully"));
//    }
//
//    //TODO : FORCE FRONTEND TO ONLY ACCEPT THOSE TYPES
//    private boolean isValidFileType(MultipartFile file, String type) {
//        if ("image".equals(type)) {
//            return file.getContentType() != null &&
//                    (file.getContentType().startsWith("image/jpeg") ||
//                            file.getContentType().startsWith("image/png") ||
//                            file.getContentType().startsWith("image/gif"));
//        } else if ("video".equals(type)) {
//            return file.getContentType() != null &&
//                    (file.getContentType().startsWith("video/mp4") ||
//                            file.getContentType().startsWith("video/quicktime"));
//        }
//        return false;
//    }
//}
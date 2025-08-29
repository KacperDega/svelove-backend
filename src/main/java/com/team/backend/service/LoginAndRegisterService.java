package com.team.backend.service;

import com.team.backend.model.User;
import com.team.backend.model.dto.LoginResponseDto;
import com.team.backend.model.dto.RegisterRequest;
import com.team.backend.model.dto.RegisterResponseDto;
import com.team.backend.model.mapper.UserMapper;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Component
public class LoginAndRegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EncoderService encoderService;
    private final SupabaseStorageService supabaseStorageService;


    @Transactional
    public RegisterResponseDto register(RegisterRequest requestDto, List<MultipartFile> photos) throws IOException {
        final User user = userMapper.mapToUser(requestDto);
        String encodedPassword = encoderService.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);

        final User savedUser = userRepository.save(user);
        savedUser.setPhotoUrls(new ArrayList<>());
        log.info("User registered: {}", savedUser);

        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (MultipartFile photo : photos) {
                String url = supabaseStorageService.uploadImage(photo, savedUser.getId());
                uploadedUrls.add(url);
            }
//            log.info("Uploaded URLs list: {}", uploadedUrls);

            savedUser.getPhotoUrls().clear();
            savedUser.getPhotoUrls().addAll(uploadedUrls);
            userRepository.save(savedUser);

        } catch (Exception ex) {
            for (String url : uploadedUrls) {
                try {
                    supabaseStorageService.deleteImage(url);
                } catch (Exception deleteEx) {
                    log.warn("Failed to delete uploaded image during rollback: {}", url, deleteEx);
                }
            }

            throw new RuntimeException("Photo upload failed. Rollback registration.", ex);
        }

        return userMapper.mapToRegisterResponse(savedUser);
    }


    public LoginResponseDto findByLogin(String login) {

        final User user = userRepository
                .findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + login + " not found"));
        log.info("User found by login: {}", user);

        return userMapper.mapToUserResponse(user);
    }



    public LoginResponseDto deleteUser(final String login) {
        final User deleted = userRepository
                .findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + login + " not found"));
        userRepository.deleteByLogin(deleted.getLogin());

        return userMapper.mapToUserResponse(deleted);
    }
}
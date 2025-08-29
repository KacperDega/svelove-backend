package com.team.backend.controller;


import com.team.backend.model.dto.LoginResponseDto;
import com.team.backend.model.dto.RegisterRequest;
import com.team.backend.model.dto.RegisterResponseDto;
import com.team.backend.model.mapper.LoginAndRegisterMapper;
import com.team.backend.model.mapper.UserMapper;
import com.team.backend.service.LoginAndRegisterService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class LoginAndRegisterRestController
{

    private final LoginAndRegisterService loginAndRegisterService;
    private final LoginAndRegisterMapper mapper;
    private final UserMapper userMapper;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("data") @Valid RegisterRequest registerRequest,
            @RequestPart(value = "photos") List<MultipartFile> photos
    ) {

        log.info("Register request: {}", registerRequest);

        if (photos == null || photos.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("At least one photo must be uploaded.");
        }

        if (photos.size() > 5) {
            return ResponseEntity
                    .badRequest()
                    .body("Max 5 photos per user allowed.");
        }


        try {
            RegisterResponseDto responseDto = loginAndRegisterService.register(registerRequest, photos);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception ex) {
            log.error("Registration with photos failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/find/{login}")
    public ResponseEntity<LoginResponseDto> findUser(@PathVariable String login) {
        final LoginResponseDto byUsername = loginAndRegisterService.findByLogin(login);
        log.info("User found: {}", byUsername);
        return ResponseEntity.ok(byUsername);
    }


    @DeleteMapping("/delete/{login}")
    public ResponseEntity<LoginResponseDto> deleteUser(@PathVariable String login) {
        log.info("Deleting user: {}", login);
        return ResponseEntity.ok(loginAndRegisterService.deleteUser(login));
    }
}
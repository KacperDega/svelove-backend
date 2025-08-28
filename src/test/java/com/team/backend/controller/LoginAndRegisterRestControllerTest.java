package com.team.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.backend.config.security.JwtConfigProperties;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.dto.LoginRequest;
import com.team.backend.model.dto.LoginResponseDto;
import com.team.backend.model.dto.RegisterRequest;
import com.team.backend.model.dto.RegisterResponseDto;
import com.team.backend.model.mapper.LoginAndRegisterMapper;
import com.team.backend.model.mapper.UserMapper;
import com.team.backend.service.LoginAndRegisterService;
import com.team.backend.service.EncoderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginAndRegisterRestController.class)
class LoginAndRegisterRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private LoginAndRegisterService loginAndRegisterService;

    @MockitoBean
    private LoginAndRegisterMapper mapper;

    @MockitoBean
    private EncoderService encoderService;

    @MockitoBean
    private JwtConfigProperties jwtConfigProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        RegisterRequest registerRequest =
                new RegisterRequest("testUser", "testLogin", "password123", Sex.MALE, Preference.BOTH, "I am a test user", 25,
                20, 30, 1L, List.of(1L) );
        RegisterResponseDto expectedResponse = new RegisterResponseDto("testUser", "testLogin", "REGISTERED");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(registerRequest)
        );

        MockMultipartFile photo = new MockMultipartFile(
                "photos",
                "photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image content".getBytes()
        );

        // When
        when(loginAndRegisterService.register(any(RegisterRequest.class), any(MultipartFile[].class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(multipart("/register")
                        .file(jsonPart)
                        .file(photo)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.login").value("testLogin"))
                .andExpect(jsonPath("$.message").value("REGISTERED"));
    }

    @Test
    @WithMockUser
    void shouldFindUserSuccessfully() throws Exception {
        // Given
        String login = "testLogin";
        LoginResponseDto loginResponseDto = new LoginResponseDto("testUser", "testLogin", "testPassword");

        // When
        when(loginAndRegisterService.findByLogin(login)).thenReturn(loginResponseDto);

        // Then
        mockMvc.perform(get("/find/{login}", login)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.login").value("testLogin"))
                .andExpect(jsonPath("$.password").value("testPassword"));
    }
}
package com.team.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.backend.config.security.JwtConfigProperties;
import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Enum.Sex;
import com.team.backend.model.Hobby;
import com.team.backend.model.dto.LoginRequest;
import com.team.backend.model.dto.LoginResponseDto;
import com.team.backend.model.dto.RegisterRequest;
import com.team.backend.model.dto.RegisterResponseDto;
import com.team.backend.model.mapper.LoginAndRegisterMapper;
import com.team.backend.model.mapper.UserMapper;
import com.team.backend.service.LoginAndRegisterService;
import com.team.backend.service.PasswordEncoderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private PasswordEncoderService passwordEncoderService;

    @MockitoBean
    private JwtConfigProperties jwtConfigProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testLogin", "password123", Sex.MALE, Preference.BOTH, "I am a test user", 25,
                20, 30, "Warsaw", List.of(Hobby.fromString("Reading")));
        LoginRequest loginRequest = new LoginRequest("testUser", "testLogin", "encodedPassword123");
        LoginResponseDto loginResponseDto = new LoginResponseDto("testUser", "testLogin", "testPassword");
        RegisterResponseDto expectedResponse = new RegisterResponseDto("testUser", "testLogin", "REGISTERED");

        // When
        when(mapper.fromRegisterRequestDto(registerRequest)).thenReturn(loginRequest);
        when(loginAndRegisterService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);
        when(mapper.fromRegisterRequestDto(registerRequest)).thenReturn(loginRequest);
        when(passwordEncoderService.encodePassword(any())).thenReturn("encodedPassword123");


        // Then
        mockMvc.perform(post("/register")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
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
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.login").value("testLogin"))
                .andExpect(jsonPath("$.password").value("testPassword"));
    }
}
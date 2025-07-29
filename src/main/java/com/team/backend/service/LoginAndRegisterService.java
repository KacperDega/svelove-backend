package com.team.backend.service;

import com.team.backend.model.User;
import com.team.backend.model.dto.LoginResponseDto;
import com.team.backend.model.dto.RegisterRequest;
import com.team.backend.model.dto.RegisterResponseDto;
import com.team.backend.model.mapper.UserMapper;
import com.team.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class LoginAndRegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EncoderService encoderService;


    public RegisterResponseDto register(RegisterRequest requestDto) {
        final User user = userMapper.mapToUser(requestDto);
        String encodedPassword = encoderService.encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        final User saved = userRepository.save(user);
        log.info("User registered: {}", saved);

        return userMapper.mapToRegisterResponse(saved);
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
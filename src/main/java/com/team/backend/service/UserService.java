package com.team.backend.service;

import com.team.backend.model.Enum.Preference;
import com.team.backend.model.Hobby;
import com.team.backend.model.User;
import com.team.backend.model.dto.PasswordChangeRequest;
import com.team.backend.model.dto.UserProfileUpdateDto;
import com.team.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final HobbyService hobbyService;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserDetails loadUserByLogin(String username) {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User updateUser(User user, UserProfileUpdateDto updateDto, boolean config) {
        if (!config) {
            user.setUsername(updateDto.username());
        }
        user.setPreference(Preference.valueOf(updateDto.preference()));

        List<Hobby> hobbies = hobbyService.getHobbiesByIdList(updateDto.hobbyIds());
        user.setHobbies(hobbies);

        user.setDescription(updateDto.description());
        user.setLocalization(updateDto.localization());
        user.setAge_min(updateDto.ageMin());
        user.setAge_max(updateDto.ageMax());

        return userRepository.save(user);
    }

    public User changePassword(String username, PasswordChangeRequest request) {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!passwordEncoderService.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoderService.encodePassword(request.newPassword()));
        return userRepository.save(user);
    }
}

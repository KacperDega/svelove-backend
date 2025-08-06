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
    private final EncoderService encoderService;
    private final HobbyService hobbyService;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        String login = authentication.getName();

        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    public UserDetails loadUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    public User updateUser(User user, UserProfileUpdateDto updateDto) {
        user.setUsername(updateDto.username());
        user.setPreference(Preference.valueOf(updateDto.preference()));

        List<Hobby> hobbies = hobbyService.getHobbiesByIdList(updateDto.hobbyIds());
        user.setHobbies(hobbies);

        user.setDescription(updateDto.description());
        user.setLocalization(updateDto.localization());
        user.setAge(updateDto.age());
        user.setAge_min(updateDto.ageMin());
        user.setAge_max(updateDto.ageMax());

        return userRepository.save(user);
    }

    public User changePassword(String login, PasswordChangeRequest request) {
        User user = getUserByLogin(login);

        if (!encoderService.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(encoderService.encodePassword(request.newPassword()));
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}

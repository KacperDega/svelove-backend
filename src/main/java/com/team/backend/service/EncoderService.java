package com.team.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class EncoderService {

    private final PasswordEncoder passwordEncoder;

    public EncoderService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encodeUserIdToFolderHash(Long userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(userId.toString().getBytes(StandardCharsets.UTF_8));

            byte[] shortHash = Arrays.copyOfRange(hash, 0, 7);
            BigInteger bigInt = new BigInteger(1, shortHash);
            String base36 = bigInt.toString(36);

            return String.format("%10s", base36).replace(' ', '0');
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate folder hash", e);
        }
    }


}

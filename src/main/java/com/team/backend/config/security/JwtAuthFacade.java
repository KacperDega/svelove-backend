package com.team.backend.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.team.backend.config.security.dto.JwtResponseDto;
import com.team.backend.config.security.dto.TokenRequestDto;
import com.team.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Map;
import java.util.function.Function;

@Log4j2
@Component
@AllArgsConstructor
public class JwtAuthFacade {

    private final AuthenticationManager authenticatorManager;
    private final JwtConfigProperties properties;
    private final UserRepository userRepository;

    public JwtResponseDto authenticateAndGenerateToken(TokenRequestDto tokenRequestDto)
    {
        Authentication authenticate = authenticatorManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenRequestDto.login(), tokenRequestDto.password())
        );
        final User principal = (User) authenticate.getPrincipal();
        String token = createToken(principal);
        String login = principal.getUsername();
        com.team.backend.model.User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return new JwtResponseDto(user.getUsername(),user.getId(),token, user.getPhotoUrls().getFirst());
    }

    private String createToken(final User user) {
        String secretKey =  properties.secret();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        ZonedDateTime localTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Warsaw"));
        Instant now = localTime.toInstant();
        Instant expireAt = now.plus(Duration.ofHours(12));
        String issuer = "Svelove";

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token) {
        return getClaim(token, claims -> claims.get("sub").asString());
    }
    public <T> T getClaim(String token, Function<Map<String, Claim>, T> claimsResolver) {
        Map<String, Claim> claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Map<String, Claim> getAllClaims(String token) {
        return decodeJWT(token).getClaims();
    }
    public DecodedJWT decodeJWT(String token) throws TokenExpiredException {
        Algorithm algorithm = Algorithm.HMAC256(properties.secret());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

}
package ru.netology.diplomcloud.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.netology.diplomcloud.entity.User;
import ru.netology.diplomcloud.repository.SecurityRepository;
import ru.netology.diplomcloud.repository.UserRepository;
import ru.netology.diplomcloud.util.AppConstant;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SecurityService {
    @Value("${jwt.signing.key}")
    private String signingKey;
    @Value("${jwt.key.expiration}")
    private Long tokenExpiration;

    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private SecretKey key;

    private SecretKey generatedSecretKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    public String generatedAuthToken(Authentication authentication) {
        return Jwts.builder()
            .claims(
                Map.of(
                    AppConstant.USERNAME, authentication.getName()))
            .expiration(new Date(new Date().getTime() + tokenExpiration))
            .subject(authentication.getName())
            .signWith(generatedSecretKey())
            .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(generatedSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isValidAuthToken(String authToken) {
        Claims claims = Jwts.parser()
            .verifyWith(generatedSecretKey())
            .build()
            .parseSignedClaims(authToken)
            .getPayload();

        String username = String.valueOf(claims.get(AppConstant.USERNAME));
        User user = userRepository.findByUsername(username);
        Optional<String> tokenFromMemory = securityRepository.getAuthTokenByUsername(username);

        return claims.getExpiration().after(new Date())
            && user != null
            && tokenFromMemory.isPresent()
            && tokenFromMemory.get().equals(authToken);
    }
}

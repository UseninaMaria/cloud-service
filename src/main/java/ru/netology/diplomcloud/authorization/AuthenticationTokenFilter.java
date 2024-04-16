package ru.netology.diplomcloud.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.diplomcloud.service.SecurityService;

import java.io.IOException;
import java.util.Optional;

import static ru.netology.diplomcloud.util.AppConstant.AUTH_TOKEN;
import static ru.netology.diplomcloud.util.AppConstant.BEARER;
import static ru.netology.diplomcloud.util.AppConstant.USERNAME;

@RequiredArgsConstructor
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private final SecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws
        ServletException, IOException {
        String authorizationKey = request.getHeader(AUTH_TOKEN);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith(BEARER)) {
            authorizationKey = authorizationKey.replace(BEARER, "").trim();
            try {
                if (securityService.isValidAuthToken(authorizationKey)) {
                    Claims claims = securityService.getClaims(authorizationKey);
                    String username = String.valueOf(claims.get(USERNAME));
                    Authentication authentication = new UsernamePasswordAuthentication(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login");
    }
}

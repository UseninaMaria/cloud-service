package ru.netology.diplomcloud.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.diplomcloud.dto.request.LoginRequestDto;
import ru.netology.diplomcloud.dto.response.ExceptionResponseDto;
import ru.netology.diplomcloud.dto.response.LoginResponseDto;
import ru.netology.diplomcloud.repository.SecurityRepository;
import ru.netology.diplomcloud.service.SecurityService;

import java.io.IOException;

import static ru.netology.diplomcloud.util.AppConstant.AUTH_TOKEN;

@RequiredArgsConstructor
@Component
@Slf4j
public class LoginAuthenticationFilter extends OncePerRequestFilter {
    private final SecurityService securityService;
    private final SecurityRepository securityRepository;
    private final UsernamePasswordAuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException {
        if (request.getHeader(AUTH_TOKEN) == null) {
            String bodyJson = request.getReader().readLine();
            if (bodyJson != null) {
                ObjectMapper mapper = new ObjectMapper();
                LoginRequestDto userDto = mapper.readValue(bodyJson, LoginRequestDto.class);
                String username = userDto.getLogin();
                String password = userDto.getPassword();
                try {
                    Authentication authentication = new UsernamePasswordAuthentication(username, password, null);
                    authentication = authenticationProvider.authenticate(authentication);
                    String authToken = securityService.generatedAuthToken(authentication);

                    securityRepository.putAuthToken(username, authToken);
                    log.info("User " + username + " authentication. Token: " + authToken);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(mapper.writeValueAsString(new LoginResponseDto(authToken)));
                    response.getWriter().flush();
                } catch (BadCredentialsException | ObjectNotFoundException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(mapper.writeValueAsString(
                        new ExceptionResponseDto(HttpServletResponse.SC_BAD_REQUEST, e.getMessage())));
                    response.getWriter().flush();
                }
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/login");
    }
}

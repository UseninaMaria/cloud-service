package ru.netology.diplomcloud.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.netology.diplomcloud.dto.UserAdapter;
import ru.netology.diplomcloud.service.UserServiceImpl;

@RequiredArgsConstructor
@Component
public class UsernamePasswordAuthenticationProvider {
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        UserAdapter userAdapter = userService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, userAdapter.getPassword())) {
            return new UsernamePasswordAuthentication(username, password, null);
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}

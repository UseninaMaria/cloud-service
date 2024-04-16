package ru.netology.diplomcloud.authorization;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.netology.diplomcloud.repository.SecurityRepository;
import ru.netology.diplomcloud.service.SecurityService;

import java.io.IOException;
import java.util.Optional;

import static ru.netology.diplomcloud.util.AppConstant.AUTH_TOKEN;
import static ru.netology.diplomcloud.util.AppConstant.BEARER;
import static ru.netology.diplomcloud.util.AppConstant.USERNAME;

@RequiredArgsConstructor
@Component
public class LogoutHandler extends
    HttpStatusReturningLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    SecurityService securityService;
    @Autowired
    SecurityRepository securityRepository;

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication)
        throws IOException {

        String authorizationKey = request.getHeader(AUTH_TOKEN);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith(BEARER)) {
            authorizationKey = authorizationKey.replace(BEARER, "").trim();
            Claims claims = securityService.getClaims(authorizationKey);
            String username = String.valueOf(claims.get(USERNAME));

            securityRepository.removeAuthTokenByUsername(username);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        super.onLogoutSuccess(request, response, authentication);
    }
}

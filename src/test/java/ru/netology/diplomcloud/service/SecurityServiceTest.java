package ru.netology.diplomcloud.service;

import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import ru.netology.diplomcloud.authorization.UsernamePasswordAuthentication;
import ru.netology.diplomcloud.repository.SecurityRepository;
import ru.netology.diplomcloud.util.AppConstant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.netology.diplomcloud.Utils.USER_NAME;

@SpringBootTest
public class SecurityServiceTest {

    @Autowired
    SecurityService securityService;

    @Autowired
    SecurityRepository securityRepository;

    @Test
    public void authTokentest() {
        Authentication authentication = new UsernamePasswordAuthentication(USER_NAME, null, null);
        String token = securityService.generatedAuthToken(authentication);
        securityRepository.putAuthToken(USER_NAME, token);

        Claims claims = securityService.getClaims(token);
        String username = String.valueOf(claims.get(AppConstant.USERNAME));
        boolean isValidToken = securityService.isValidAuthToken(token);

        assertEquals(username, USER_NAME);
        assertTrue(isValidToken);
    }
}

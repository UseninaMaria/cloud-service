package ru.netology.diplomcloud.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netology.diplomcloud.dto.UserAdapter;
import ru.netology.diplomcloud.entity.User;
import ru.netology.diplomcloud.repository.UserRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static ru.netology.diplomcloud.Utils.USER_NAME;
import static ru.netology.diplomcloud.Utils.USER_NAME_WRONG;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    @Test
    public void loadUserByUsername() {
        User user = userRepository.findByUsername(USER_NAME);
        UserAdapter userDetail = userService.loadUserByUsername(USER_NAME);

        assertEquals(userDetail.getUsername(), user.getUsername());
        assertEquals(userDetail.getPassword(), user.getPassword());
    }

    @Test
    public void loadUserByUsernameException() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(USER_NAME_WRONG));
    }
}

package ru.netology.diplomcloud.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.diplomcloud.dto.UserAdapter;
import ru.netology.diplomcloud.entity.User;
import ru.netology.diplomcloud.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserAdapter loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserAdapter(user);
    }
}

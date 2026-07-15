package com.yourdomain.ecommerce.security;

import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.enums.UserStatus;
import com.yourdomain.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .enabled(user.getStatus() == UserStatus.ACTIVE)
                .locked(user.getStatus() == UserStatus.LOCKED)
                .build();
    }
}

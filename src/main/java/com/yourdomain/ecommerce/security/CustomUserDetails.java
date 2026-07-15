package com.yourdomain.ecommerce.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Set<String> roles;
    private final boolean enabled;
    private final boolean locked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

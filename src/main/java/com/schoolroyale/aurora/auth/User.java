package com.schoolroyale.aurora.auth;

import com.schoolroyale.aurora.auth.message.AuthRequest;
import com.schoolroyale.aurora.auth.role.Role;
import com.schoolroyale.aurora.auth.role.Roles;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

@Document("api-user")
public record User(
        String username,
        String password,
        boolean enabled,
        Collection<Role> role
) implements UserDetails {

    public static User from(AuthRequest request, PasswordEncoder encoder) {
        return new User(request.username(), encoder.encode(request.password()), true, Roles.forNewUser());
    }

    public Role maxRole() {
        return role.stream()
                .max(Role.comparator())
                .orElse(Role.ROLE_USER);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

package com.schoolroyale.aurora.auth.user;

import com.schoolroyale.aurora.auth.message.AuthRequest;
import com.schoolroyale.aurora.auth.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
@Document("api-user")
public final class ApiUser implements UserDetails {

    @Serial private static final long serialVersionUID = 0L;

    private String username;
    private String password;
    private String minecraftAccountId;

    private Collection<String> role;

    public static ApiUser from(AuthRequest request, PasswordEncoder passwordEncoder, Role defaultRole) {
        return new ApiUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.minecraftAccountId(),
                List.of(defaultRole.name())
        );
    }

    public static ApiUser from(Jwt token) {
        return new ApiUser(
                token.getSubject(),
                token.getClaimAsString("password"),
                token.getClaimAsString("minecraftAccountId"),
                token.getClaimAsStringList("role")
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.stream()
                .map(SimpleGrantedAuthority::new)
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String minecraftAccountId() {
        return minecraftAccountId;
    }

    public Collection<String> role() {
        return role;
    }

    public UserDetails toUserDetails() {
        return this;
    }

    public Authentication authentication() {
        return UsernamePasswordAuthenticationToken.authenticated(this, password, getAuthorities());
    }
}

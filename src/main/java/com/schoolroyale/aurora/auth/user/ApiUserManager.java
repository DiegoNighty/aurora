package com.schoolroyale.aurora.auth.user;

import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ApiUserManager implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private final ApiUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiUserManager(ApiUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(ApiUser::toUserDetails);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        var apiUser = (ApiUser) user;
        ((ApiUser) user).setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(apiUser)
                .map(ApiUser::toUserDetails);
    }
}

package com.schoolroyale.aurora.auth.role;

import java.util.Collection;
import java.util.List;

public class Roles {

    public static final String ADMIN = "hasRole('ADMIN')";
    public static final String USER = "hasRole('USER')";

    public static Collection<Role> forNewUser() {
        return List.of(Role.ROLE_USER);
    }

    public static Role maxRoleFromAuthorities(Collection<String> authorities) {
        return authorities.stream()
                .map(Role::valueOf)
                .max(Role.comparator())
                .orElse(Role.ROLE_USER);
    }

}

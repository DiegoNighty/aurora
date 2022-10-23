package com.schoolroyale.aurora.auth.role;

import java.util.Collection;
import java.util.List;

public class Roles {

    public static final String ADMIN = "hasRole('ADMIN')";
    public static final String USER = "hasRole('USER')";

    public static Collection<Role> forNewUser() {
        return List.of(Role.ROLE_USER);
    }

}

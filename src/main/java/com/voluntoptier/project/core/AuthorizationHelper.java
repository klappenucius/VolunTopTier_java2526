package com.voluntoptier.project.core;

import com.voluntoptier.project.entities.Role;
import com.voluntoptier.project.entities.User;

import java.util.Objects;

public class AuthorizationHelper {

    private AuthorizationHelper() {}
    static void requireAdmin(User actor) {
        Objects.requireNonNull(actor, "actor");
        if (!(actor.getRole()== Role.ADMIN)) {
            throw new SecurityException("Admin role required to perform this action.");
        }
    }

    static void ensureEditRights(User actor, User target) {
        boolean isAdmin = actor.getRole()==Role.ADMIN;
        boolean isSelf = actor.getId() == target.getId();

        if (!isAdmin && !isSelf) {
            throw new SecurityException("Not allowed to change other user's data.");
        }
    }
}

package com.voluntoptier.project.utils;

import com.voluntoptier.project.entities.User;

public class SessionUtil {
    private static User currentUser;

    private SessionUtil() {}

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SessionUtil.currentUser = currentUser;
    }
}

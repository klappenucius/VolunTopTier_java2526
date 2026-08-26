package com.voluntoptier.project.utils;

import com.voluntoptier.project.entities.Role;

public class PermissionsUtil {
    public static boolean isModerator (Role role) {
        if (role == role.MODERATOR) {return true;}
        else {return false;}
    }
    public static boolean isAdmin(Role role) {
        if (role == role.ADMIN) {return true;}
        else {return false;}
    }
}

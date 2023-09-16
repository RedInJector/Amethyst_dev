package com.rij.amethyst_dev.Enums;

public enum UserRoles {
    PLAYER,
    EDITOR,
    MODERATOR,
    ADMIN;

    public boolean hasPermission(UserRoles requiredRole) {
        return this.ordinal() >= requiredRole.ordinal();
    }
}

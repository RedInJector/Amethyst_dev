package com.rij.amethyst_dev.jsons.minecraftAuth;

import java.util.Objects;

public class MinecraftSession {
    private String ip;
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (obj instanceof MinecraftSession session) {
            return Objects.equals(ip, session.ip) && Objects.equals(name, session.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, name);
    }
}

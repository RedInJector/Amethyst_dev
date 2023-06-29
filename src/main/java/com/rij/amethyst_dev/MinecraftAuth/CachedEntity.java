package com.rij.amethyst_dev.MinecraftAuth;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Getter
public class CachedEntity {
    private final String minecraftName;
    private final LocalDateTime maxTime;
    private final CompletableFuture future;
    private final Message message;

    public CachedEntity(String minecraftName, CompletableFuture future, Message message) {
        this.future = future;
        this.minecraftName = minecraftName;
        this.message = message;
        maxTime = LocalDateTime.now().plusSeconds(60);
    }
}

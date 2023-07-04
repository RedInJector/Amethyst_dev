package com.rij.amethyst_dev.MinecraftAuth;

import com.rij.amethyst_dev.jsons.minecraftAuth.MinecraftSession;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Getter
public class CachedEntity {
    private final String minecraftName;
    private final LocalDateTime maxTime;
    private final CompletableFuture<ResponseEntity<String>> future;
    private final Message message;
    private final MinecraftSession minecraftSession;

    public CachedEntity(String minecraftName, CompletableFuture<ResponseEntity<String>> future, Message message, MinecraftSession minecraftSession) {
        this.future = future;
        this.minecraftName = minecraftName;
        this.message = message;
        this.minecraftSession = minecraftSession;
        maxTime = LocalDateTime.now().plusSeconds(60);
    }
}

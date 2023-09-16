package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.Helpers.RandomStringGenerator;
import com.rij.amethyst_dev.MinecraftAuth.CachedEntity;
import com.rij.amethyst_dev.MinecraftAuth.MinecraftSession;
import com.rij.amethyst_dev.MinecraftAuth.SessionManager;
import com.rij.amethyst_dev.Routes.AuthRoute;
import com.rij.amethyst_dev.bot.DiscordEventHandlers.MessageReaction;
import com.rij.amethyst_dev.models.Userdb.User;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Getter
@Service
public class MCserverAuthService implements ApplicationListener<ContextRefreshedEvent> {
    private final Map<String, CachedEntity> Authqueue = new HashMap<>();
    private final Map<String, String> NameCode = new HashMap<>();

    private final SessionManager sessionManager = new SessionManager(120);

    private final DiscordBotService discordBotService;
    private final ScheduledExecutorService executorService;


    Logger logger = LoggerFactory.getLogger(MCserverAuthService.class);
    public MCserverAuthService(DiscordBotService discordBotService) {
        this.discordBotService = discordBotService;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void addToAuthQueue(User user, CompletableFuture<ResponseEntity<String>> response, MinecraftSession minecraftSession){
        String buttonID = RandomStringGenerator.generate(8);

        Message message = discordBotService.sendAuthentiticationMessage(user, buttonID);

        CachedEntity cachedEntity = new CachedEntity(user.getMinecraftPlayer().getPlayerName(), response, message, minecraftSession);
        Authqueue.put(buttonID, cachedEntity);
        NameCode.put(user.getMinecraftPlayer().getPlayerName(), buttonID);

        executorService.schedule(() -> removeElements(buttonID), 61, TimeUnit.SECONDS);
    }
    private void removeElements(String buttonID){
        if(Authqueue.containsKey(buttonID)) {
            String name = Authqueue.get(buttonID).getMinecraftName();
            NameCode.remove(name);
        }

        if(!Authqueue.containsKey(buttonID))
            return;

        Authqueue.get(buttonID).getMessage().delete().queue();
        Authqueue.remove(buttonID);
    }

    public void PlayerLeft(User user){
        String playername = user.getMinecraftPlayer().getPlayerName();
        if(!NameCode.containsKey(playername)){
            return;
        }

        String btncode = NameCode.get(playername);
        if(Authqueue.containsKey(btncode))
            Authqueue.get(btncode).getFuture().complete(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden"));

        removeElements(btncode);
    }
    private void saveSession(CachedEntity entity){
        MinecraftSession session = entity.getMinecraftSession();
        sessionManager.saveSession(session);
    }


    public void ReactToButton(String buttonID){
        CachedEntity cachedEntity = Authqueue.get(buttonID);

        // TODO: STUPID BUGFIX Cannot invoke "com.rij.amethyst_dev.MinecraftAuth.CachedEntity.getMaxTime()" because "cachedEntity" is null
        if(cachedEntity == null)
            return;

        if(LocalDateTime.now().isAfter(cachedEntity.getMaxTime())){
            removeElements(buttonID);
            return;
        }
        saveSession(cachedEntity);
        removeElements(buttonID);
        cachedEntity.getFuture().complete(ResponseEntity.ok("Something happened successfully"));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        discordBotService.RegisterListener(new MessageReaction(this));
    }

    public boolean isValid(MinecraftSession session){
        return sessionManager.isValid(session);
    }


}

package com.rij.amethyst_dev.MinecraftAuth;

import com.rij.amethyst_dev.Helpers.RandomStringGenerator;
import com.rij.amethyst_dev.bot.DiscordBotService;
import com.rij.amethyst_dev.bot.DiscordEventHandlers.MessageReaction;
import com.rij.amethyst_dev.jsons.minecraftAuth.MinecraftSession;
import com.rij.amethyst_dev.models.Userdb.User;
import net.dv8tion.jda.api.entities.Message;
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


@Service
public class MCserverAuthService implements ApplicationListener<ContextRefreshedEvent> {
    private final Map<String, CachedEntity> Authqueue = new HashMap<>();
    private final Map<String, String> NameCode = new HashMap<>();
    private final Map<String, String> CodeName = new HashMap<>();
    //private final Map<String, MinecraftSession> CodeSession  = new HashMap<>();

    private final SessionManager sessionManager = new SessionManager(120);

    private final DiscordBotService discordBotService;
    private final ScheduledExecutorService executorService;

    public MCserverAuthService(DiscordBotService discordBotService) {
        this.discordBotService = discordBotService;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void addToAuthQueue(User user, CompletableFuture<ResponseEntity<String>> future, MinecraftSession minecraftSession){
        String buttinID = RandomStringGenerator.generate(8);
        Message message = discordBotService.sendAuthentiticationMessage(user, buttinID);

        CachedEntity cachedEntity = new CachedEntity(user.getMinecraftPlayer().getPlayerName(), future, message, minecraftSession);
        Authqueue.put(buttinID, cachedEntity);
        NameCode.put(user.getMinecraftPlayer().getPlayerName(), buttinID);
        CodeName.put(buttinID, user.getMinecraftPlayer().getPlayerName());

        executorService.schedule(() -> removeElements(buttinID), 61, TimeUnit.SECONDS);
        /*
        System.out.println("--------------------------------------");
        for (Map.Entry<String, UnnamedModel> entry : Authqueue.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getMinecraftName());
        }
        for (Map.Entry<String, String> entry : NameCode.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println("--------------------------------------");
        */
    }
    private void removeElements(String buttonID){
        if(CodeName.containsKey(buttonID)) {
            String name = CodeName.get(buttonID);
            CodeName.remove(buttonID);
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
    private void saveSession(String buttonID){
        if(!Authqueue.containsKey(buttonID))
            return;

        CachedEntity entity = Authqueue.get(buttonID);
        MinecraftSession session = entity.getMinecraftSession();

        sessionManager.saveSession(session);
    }


    public void ReactToButton(String buttonID){
        CachedEntity cachedEntity = Authqueue.get(buttonID);

        // TODO: STUPID BUGFIX Cannot invoke "com.rij.amethyst_dev.MinecraftAuth.CachedEntity.getMaxTime()" because "cachedEntity" is null
        if(cachedEntity == null)
            return;

        if(LocalDateTime.now().isAfter(cachedEntity.getMaxTime())){
            Authqueue.remove(buttonID);
            removeElements(buttonID);
            return;
        }
        saveSession(buttonID);
        removeElements(buttonID);
        cachedEntity.getFuture().complete(ResponseEntity.ok("Something happened successfully"));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        discordBotService.RegisterListener(new MessageReaction(this));
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}

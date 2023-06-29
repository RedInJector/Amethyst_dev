package com.rij.amethyst_dev.bot;

import com.rij.amethyst_dev.MinecraftAuth.MCserverAuthService;
import com.rij.amethyst_dev.bot.DiscordEventHandlers.GuildJoin;
import com.rij.amethyst_dev.bot.DiscordEventHandlers.MessageReaction;
import com.rij.amethyst_dev.webSockets.GuildSocket.GuildSocketService;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DiscordBot implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${discord.bot.token}")
    private String Token;
    @Value("${discord.mainguildid}")
    private String GuildID;

    private JDA jda;

    private final MessageSource messageSource;
    private final GuildSocketService guildSocketService;

    public DiscordBot(MessageSource messageSource, GuildSocketService guildSocketService) {
        this.messageSource = messageSource;
        this.guildSocketService = guildSocketService;
    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            jda = JDABuilder.createDefault(Token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    //.addEventListeners(new MyEventListener()) // Add your event listener(s)
                    .setActivity(Activity.playing("Hello, Discord!")) // Set the bot's activity
                    .build().awaitReady();

            System.out.println("Discord Bot Started");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        jda.addEventListener(new GuildJoin(GuildID, guildSocketService));
        //jda.addEventListener(new MessageReaction(jda));

        jda.updateCommands().addCommands(
                Commands.slash("hello", "a")
        ).queue();
    }

}

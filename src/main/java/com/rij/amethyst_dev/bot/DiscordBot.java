package com.rij.amethyst_dev.bot;

import com.rij.amethyst_dev.bot.DiscordEventHandlers.GuildJoin;
import com.rij.amethyst_dev.webSockets.GuildSocket.GuildSocketService;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
    @Value("${discord.emergencycategoryid}")
    private String emergencycategoryid;

    private JDA jda;

    private final MessageSource messageSource;
    private final ApplicationEventPublisher eventPublisher;

    public DiscordBot(MessageSource messageSource, ApplicationEventPublisher eventPublisher) {
        this.messageSource = messageSource;
        this.eventPublisher = eventPublisher;
    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            jda = JDABuilder.createDefault(Token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    //.addEventListeners(new MyEventListener()) // Add your event listener(s)
                    .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setActivity(Activity.playing("Hello, Discord!")) // Set the bot's activity
                    .build().awaitReady();

            System.out.println("Discord Bot (" + jda.getSelfUser().getName() +") Started");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        jda.addEventListener(new GuildJoin(GuildID, eventPublisher));

        jda.updateCommands().addCommands(
                Commands.slash("hello", "a")
        ).queue();
    }

}

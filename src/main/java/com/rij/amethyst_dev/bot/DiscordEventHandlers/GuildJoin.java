package com.rij.amethyst_dev.bot.DiscordEventHandlers;


import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationEventPublisher;


public class GuildJoin extends ListenerAdapter {

    private final String GuildID;
    private final ApplicationEventPublisher eventPublisher;

    public GuildJoin(String guildID, ApplicationEventPublisher eventPublisher) {
        GuildID = guildID;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String guildid = event.getGuild().getId();
        if(!guildid.equals(this.GuildID))
            return;


        eventPublisher.publishEvent(new com.rij.amethyst_dev.events.DiscordRelated.GuildJoin(this, event));


        /*
        String discordid = event.getUser().getId();
        guildSocketService.sendMessage(discordid);
        */
    }
}

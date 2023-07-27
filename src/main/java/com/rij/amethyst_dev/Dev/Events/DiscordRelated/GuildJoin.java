package com.rij.amethyst_dev.Dev.Events.DiscordRelated;


import lombok.Getter;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.springframework.context.ApplicationEvent;

@Getter
public class GuildJoin extends ApplicationEvent {

    private final GuildMemberJoinEvent event;
    public GuildJoin(Object source, GuildMemberJoinEvent event) {
        super(source);
        this.event = event;
    }
}

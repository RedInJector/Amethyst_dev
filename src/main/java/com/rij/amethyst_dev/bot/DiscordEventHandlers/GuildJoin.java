package com.rij.amethyst_dev.bot.DiscordEventHandlers;


import com.rij.amethyst_dev.webSockets.GuildSocket.GuildSocketService;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


public class GuildJoin extends ListenerAdapter {

    private final String GuildID;
    private final GuildSocketService guildSocketService;

    public GuildJoin(String guildID, GuildSocketService guildSocketService) {
        GuildID = guildID;
        this.guildSocketService = guildSocketService;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String guildid = event.getGuild().getId();
        if(!guildid.equals(this.GuildID))
            return;

        String discordid = event.getUser().getId();
        System.out.println(discordid);

        guildSocketService.sendMessage(discordid);
    }

}

package com.rij.amethyst_dev.bot.DiscordEventHandlers;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class RolesChange extends ListenerAdapter {


    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {

        event.getUser().getId();
    }
}

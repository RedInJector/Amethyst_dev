package com.rij.amethyst_dev.bot.InternalEventHandlers;

import com.rij.amethyst_dev.bot.DiscordBot;
import com.rij.amethyst_dev.events.UserPayedEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GiveRoleOnPay extends Handler<UserPayedEvent>{
    @Autowired
    public GiveRoleOnPay(DiscordBot botConfig) {
        super(botConfig);
    }

    @Override
    public void onEvent(UserPayedEvent event) {

        User discordUser = jda.retrieveUserById(event.getUser().getDiscordUser().getDiscordId()).complete();


        Guild guild = botConfig.getJda().getGuildById(botConfig.getGuildID());

        guild.retrieveMemberById(event.getUser().getDiscordUser().getDiscordId()).queue(
                member1 -> guild.addRoleToMember(member1, guild.getRoleById("964572564166938714")).queue());
    }

}
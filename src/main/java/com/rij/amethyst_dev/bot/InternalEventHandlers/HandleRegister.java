package com.rij.amethyst_dev.bot.InternalEventHandlers;

import com.rij.amethyst_dev.bot.DiscordBot;
import com.rij.amethyst_dev.events.UserRegisteredEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class HandleRegister extends Handler<UserRegisteredEvent>{

    @Autowired
    public HandleRegister(DiscordBot botConfig) {
        super(botConfig);
        System.out.println("HERE!");
    }

    @Override
    public void onEvent(UserRegisteredEvent event) {
        User discordUser = jda.retrieveUserById(event.getUser().getDiscordUser().getDiscordId()).complete();
        PrivateChannel channel = discordUser.openPrivateChannel().complete();

        String message = getMessage("welcome.message");
        String reply = MessageFormat.format(message, discordUser.getId());
        channel.sendMessage(reply).queue();
    }
}

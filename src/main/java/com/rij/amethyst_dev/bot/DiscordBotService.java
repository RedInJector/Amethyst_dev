package com.rij.amethyst_dev.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

@Service
public class DiscordBotService {
    private final DiscordBot discordBot;


    public DiscordBotService(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    public boolean isUserOnServer(String discordid){
        Guild guild = discordBot.getJda().getGuildById(discordBot.getGuildID());

        //User duser1 = discordBot.getJda().retrieveUserById(discordid).complete();

        try {
            Member duser = guild.retrieveMemberById(discordid).complete();
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public Message sendAuthentiticationMessage(com.rij.amethyst_dev.models.Userdb.User user, String buttonID){
        User duser = discordBot.getJda().retrieveUserById(user.getDiscordUser().getDiscordId()).complete();

        PrivateChannel channel = duser.openPrivateChannel().complete();

        EmbedBuilder eb = EmbedGenerator.AuthEmbed(user.getMinecraftPlayer().getPlayerName());

        Message message = channel.sendMessageEmbeds(eb.build()).addActionRow(
                Button.primary(buttonID, "Accept")
        ).complete();

        return message;
    }

    public void deletemessage(Message message){
        message.delete().queue();
    }

    public void RegisterListener(ListenerAdapter listener){
        discordBot.getJda().addEventListener(listener);
    }


}

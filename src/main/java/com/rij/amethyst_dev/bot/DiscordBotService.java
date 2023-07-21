package com.rij.amethyst_dev.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

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

        EmbedBuilder eb = EmbedGenerator.AuthEmbed(user.getMinecraftPlayer().getPlayerName());

        try {
            PrivateChannel channel = duser.openPrivateChannel().complete();

            Message message = channel.sendMessageEmbeds(eb.build()).addActionRow(
                    Button.primary(buttonID, "Accept")
            ).complete();
            return message;
        }
        catch (Exception any){

            String guildid = discordBot.getGuildID();
            String categoryid = discordBot.getEmergencycategoryid();

            Member member = discordBot.getJda().getGuildById(guildid).retrieveMemberById(duser.getId()).complete();


            TextChannel textChannel = null;

            Category category = discordBot.getJda().getGuildById(guildid).getCategoryById(categoryid);

            for(Channel channel : category.getChannels()){
                if(channel.getName().toLowerCase().equals(duser.getName().toLowerCase()))
                    textChannel = (TextChannel) channel;
            }

            if(textChannel != null){
                return textChannel.sendMessageEmbeds(eb.build()).addActionRow(Button.primary(buttonID, "Прийняти")).complete();
            }

            Guild guild = discordBot.getJda().getGuildById(guildid);
            TextChannel textChannel1 = guild.getCategoryById(categoryid).createTextChannel(duser.getName().toLowerCase()).complete();


            textChannel1.upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL).queue();


            return textChannel1.sendMessageEmbeds(eb.build()).addActionRow(Button.primary(buttonID, "Прийняти")).complete();
        }

    }

    public void deletemessage(Message message){
        message.delete().queue();
    }

    public void RegisterListener(ListenerAdapter listener){
        discordBot.getJda().addEventListener(listener);
    }

    public List<Role> getGuildRoles(String discordId){
        //User user = discordBot.getJda().retrieveUserById(discordId).complete();
        Guild guild = discordBot.getJda().getGuildById(discordBot.getGuildID());

        try {
            Member duser = guild.retrieveMemberById(discordId).complete();
            List<Role> roles = duser.getRoles();
            return roles;
        }catch (Exception e){
            return null;
        }
    }


}

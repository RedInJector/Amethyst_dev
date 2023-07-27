package com.rij.amethyst_dev.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class DiscordBotService {
    private final DiscordBot discordBot;

    @Value("${DISCORD_PLAYER_ROLE_ID}")
    public String RoleID;

    Logger logger = LoggerFactory.getLogger(DiscordBotService.class);


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

        TextChannel textChannel = getUsableChannel(duser);

        textChannel.sendMessageEmbeds(eb.build());



        return textChannel.sendMessageEmbeds(eb.build()).addActionRow(Button.primary(buttonID, "Прийняти")).complete();

    }

    public void GreetFirstTime(String discordid){
        User discordUser = discordBot.getJda().retrieveUserById(discordid).complete();
        getUsableChannel(discordUser);
    }

    public void addPlayerRole(String discordid){
        JDA jda = discordBot.getJda();

        //User discordUser = jda.retrieveUserById(discordid).complete();


        Guild guild = jda.getGuildById(discordBot.getGuildID());

        guild.retrieveMemberById(discordid).queue(
                member1 -> guild.addRoleToMember(member1, guild.getRoleById(RoleID)).queue());
    }

    public void RegisterListener(ListenerAdapter listener){
        discordBot.getJda().addEventListener(listener);
    }

    @Cacheable(value = "discordRolesCache", key = "#discordId")
    public List<Role> getGuildRoles(String discordId){
        Guild guild = discordBot.getJda().getGuildById(discordBot.getGuildID());

        try {
            Member duser = guild.retrieveMemberById(discordId).complete();
            List<Role> roles = duser.getRoles();
            return roles;
        }catch (Exception e){
            return null;
        }
    }

    @CacheEvict(value = "discordRolesCache", allEntries = true)
    @Scheduled(fixedRateString = "600000")
    public void emptyRolesCache() {
        logger.info("Emptying: discordRolesCache");
    }

    private TextChannel getUsableChannel(User duser){

        try {
            return (TextChannel) duser.openPrivateChannel().complete();
        }
        catch (Exception any){

            String guildid = discordBot.getGuildID();
            String categoryid = discordBot.getEmergencycategoryid();

            Member member = discordBot.getJda().getGuildById(guildid).retrieveMemberById(duser.getId()).complete();


            Category category = discordBot.getJda().getGuildById(guildid).getCategoryById(categoryid);

            for(Channel channel : category.getChannels()){
                if(channel.getName().toLowerCase().equals(duser.getName().toLowerCase()))
                    return (TextChannel) channel;
            }

            Guild guild = discordBot.getJda().getGuildById(guildid);
            TextChannel textChannel = guild.getCategoryById(categoryid).createTextChannel(duser.getName().toLowerCase()).complete();

            textChannel.upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL).queue();

            return textChannel;
        }
    }
}

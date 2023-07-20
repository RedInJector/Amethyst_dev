package com.rij.amethyst_dev.bot.InternalEventHandlers;

import com.rij.amethyst_dev.bot.DiscordBot;
import com.rij.amethyst_dev.bot.EmbedGenerator;
import com.rij.amethyst_dev.events.UserPayedEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandlePayment extends Handler<UserPayedEvent>{
    @Autowired
    public HandlePayment(DiscordBot botConfig) {
        super(botConfig);
    }

    @Override
    public void onEvent(UserPayedEvent event) {

        User discordUser = jda.retrieveUserById(event.getUser().getDiscordUser().getDiscordId()).complete();

        try {
            PrivateChannel channel = discordUser.openPrivateChannel().complete();
            channel.sendMessageEmbeds(
                    EmbedGenerator.PaymentGreetengs(discordUser.getId()).build()
            ).complete();
        }catch (Exception e){

            String guildid = botConfig.getGuildID();
            String categoryid = botConfig.getEmergencycategoryid();

            Member member;
            try{
                member = jda.getGuildById(guildid).retrieveMemberById(discordUser.getId()).complete();
            }catch (Exception any){ return;}

            TextChannel textChannel = null;

            Category category = jda.getGuildById(guildid).getCategoryById(categoryid);

            for(Channel channel : category.getChannels()){
                if(channel.getName().toLowerCase().equals(discordUser.getName().toLowerCase()))
                    textChannel = (TextChannel) channel;
            }

            if(textChannel != null){
                    textChannel.sendMessageEmbeds(EmbedGenerator.PaymentGreetengsToGuild(discordUser.getId()).build()).complete();
                    return;
            }

            jda.getGuildById(guildid).getCategoryById(categoryid).createTextChannel(discordUser.getName().toLowerCase())
                    .queue(
                textChannel1 -> {
                    try {
                        textChannel1.upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL).queue();
                        textChannel1.sendMessageEmbeds(EmbedGenerator.PaymentGreetengsToGuild(discordUser.getId()).build()).queue();
                    }catch (Exception ignore){
                        System.out.println(ignore);
                    }
                }
            );
        }

    }

}

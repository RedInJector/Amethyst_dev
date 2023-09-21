package com.rij.amethyst_dev.EventHandlers;

import com.rij.amethyst_dev.events.DiscordRelated.GuildJoin;
import com.rij.amethyst_dev.bot.DiscordBot;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import com.rij.amethyst_dev.webSockets.GuildSocket.GuildSocketService;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class GuildJoinHandler implements ApplicationListener<GuildJoin> {

    private final DiscordBot bot;
    private final UserService userService;
    private final GuildSocketService guildSocketService;

    @Value("${DISCORD_PLAYER_ROLE_ID}")
    public String RoleID;

    public GuildJoinHandler(DiscordBot bot, UserService userService, GuildSocketService guildSocketService) {
        this.bot = bot;
        this.userService = userService;
        this.guildSocketService = guildSocketService;
    }

    @Override
    public void onApplicationEvent(GuildJoin event) {
        String duserid = event.getEvent().getUser().getId();
        Guild guild = event.getEvent().getGuild();

        User user = userService.getUserByDiscordId(duserid);
        if(user == null)
            return;

        guildSocketService.sendMessage(duserid);

        if(user.isHasPayed())
            guild.addRoleToMember(event.getEvent().getUser(), guild.getRoleById(RoleID));

    }
}

package com.rij.amethyst_dev.Dev.DTO.User.Builder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rij.amethyst_dev.DTO.AllPlaytime;
import com.rij.amethyst_dev.Dev.DTO.*;
import com.rij.amethyst_dev.Dev.DTO.User.*;
import com.rij.amethyst_dev.PlanData.PlanDataService;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.Services.OnlinePlayersStorage;
import com.rij.amethyst_dev.models.Userdb.User;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDataDTOBuilder {

    private IUserDTO user = null;
    private UserStatisticsDTO statistics = null;
    private List<DiscordRoleDTO> roles = null;
    private Long lastonline = null;


    public UserDataDTOBuilder addPrivateUserData(User user) {
        this.user = user.toPrivateDTO();
        return this;
    }

    public UserDataDTOBuilder addPublicUserData(User user) {
        this.user = user.toPublicDTO();
        return this;
    }

    public UserDataDTOBuilder addStatisticsData(PlanDataService planDataService, User user) {
        AllPlaytime allPlaytime = planDataService.getPlayTime(user);
        List<PlayTimeDateDTO> heatmap = planDataService.getHeatmapTime(user);
        Long lastonline = planDataService.getLastOnline(user);

        this.statistics = new UserStatisticsDTO(
                allPlaytime.allTimeSeconds(),
                allPlaytime.lastMonthSeconds(),
                allPlaytime.lastWeekSeconds(),
                allPlaytime.lastDaySeconds(),
                lastonline,
                heatmap
        );
        return this;
    }

    public UserDataDTOBuilder addRoles(DiscordBotService discordBotService, User user) {
        List<Role> roles = discordBotService.getGuildRoles(user.getDiscordUser().getDiscordId());
        this.roles = (roles != null) ? roles.stream().map(DTOMapper.DTOFromRole).collect(Collectors.toList()) : null;

        return this;
    }

    public UserDataDTOBuilder addLastTimeOnServer(PlanDataService planDataService, OnlinePlayersStorage onlinePlayersStorage, User user) {
        if(onlinePlayersStorage.isOnline(user.getMinecraftPlayer().getPlayerName()))
            this.lastonline = 0L;
        else
            this.lastonline = planDataService.getLastOnline(user);

        return this;
    }


    public UserDataDTO build() {
        return new UserDataDTO(
                user,
                statistics,
                roles,
                lastonline
        );
    }


}

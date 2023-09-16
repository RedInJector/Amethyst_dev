package com.rij.amethyst_dev.DTO.User.Builder;

import com.rij.amethyst_dev.DTO.DTOMapper;
import com.rij.amethyst_dev.DTO.User.DiscordRoleDTO;
import com.rij.amethyst_dev.DTO.User.PlayTimeDateDTO;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.models.Userdb.User;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.stream.Collectors;

public class UserDataDTOBuilderRework {
    private User user = null;
    private boolean isPrivate = false;
    private String time_all = "0";
    private String time_month = "0";
    private String time_week = "0";
    private String time_day = "0";
    private Long last_online = 0L;
    private List<DiscordRoleDTO> roles = null;
    private List<PlayTimeDateDTO> heatmap_data;

    public UserDataDTOBuilderRework(User user){
        this.user = user;
    }

    public UserDataDTOBuilderRework isPrivate(boolean isPrivate){
        this.isPrivate = isPrivate;
        return this;
    }

    public UserDataDTOBuilderRework addAllPlayTime(String allPlaytime){
        this.time_all = allPlaytime;
        return this;
    }
    public UserDataDTOBuilderRework addMonthPlayTime(String monthPlaytime){
        this.time_month = monthPlaytime;
        return this;
    }
    public UserDataDTOBuilderRework addWeekPlayTime(String weekPlaytime){
        this.time_week = weekPlaytime;
        return this;
    }
    public UserDataDTOBuilderRework addDayPlayTime(String dayPlaytime){
        this.time_day = dayPlaytime;
        return this;
    }


    public UserDataDTOBuilderRework addHeatmapData(List<PlayTimeDateDTO> heatmap){
        this.heatmap_data = heatmap;
        return this;
    }

    public UserDataDTOBuilderRework addRoles(DiscordBotService discordBotService, User user) {
        List<Role> roles = discordBotService.getGuildRoles(user.getDiscordUser().getDiscordId());
        this.roles = (roles != null) ? roles.stream().map(DTOMapper.DTOFromRole).collect(Collectors.toList()) : null;

        return this;
    }




}

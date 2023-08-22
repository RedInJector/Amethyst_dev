package com.rij.amethyst_dev.DTO.User;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDataDTO(
    IUserDTO user,
    UserStatisticsDTO statistics,
    List<DiscordRoleDTO> roles,
    Long lastOnline
) {
}

package com.rij.amethyst_dev.Dev.UserDTOS.Private;


import com.rij.amethyst_dev.Dev.UserDTOS.DiscordRoleDTO;
import com.rij.amethyst_dev.Dev.UserDTOS.UserStatisticsDTO;

import java.util.List;

public record PrivateUserDataDTO(
    PrivateUserDTO user,
    UserStatisticsDTO statistics,
    List<DiscordRoleDTO> roles
) {
}

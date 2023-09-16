package com.rij.amethyst_dev.DTO.User.Private;

import com.rij.amethyst_dev.DTO.User.IUserDTO;
import com.rij.amethyst_dev.Enums.UserRoles;

public record PrivateUserDTO(
        Long id,
        BaseDiscordDTO discordUser,
        String minecraftName,
        boolean hasPayed,
        boolean banned,
        boolean admin,
        boolean unbannable,
        UserRoles userRole
) implements IUserDTO {

}

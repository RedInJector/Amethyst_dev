package com.rij.amethyst_dev.Dev.DTO.User.Private;

import com.rij.amethyst_dev.Dev.DTO.User.IUserDTO;

public record PrivateUserDTO(
        Long id,
        BaseDiscordDTO discordUser,
        String minecraftName,
        boolean hasPayed,
        boolean banned,
        boolean admin,
        boolean unbannable
) implements IUserDTO {

}

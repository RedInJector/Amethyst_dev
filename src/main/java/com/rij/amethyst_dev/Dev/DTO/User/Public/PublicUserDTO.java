package com.rij.amethyst_dev.Dev.DTO.User.Public;

import com.rij.amethyst_dev.Dev.DTO.User.IUserDTO;
import com.rij.amethyst_dev.Dev.DTO.User.Private.BaseDiscordDTO;

public record PublicUserDTO(
        Long id,
        BaseDiscordDTO discordUser,
        String minecraftName,
        boolean banned
) implements IUserDTO {
}

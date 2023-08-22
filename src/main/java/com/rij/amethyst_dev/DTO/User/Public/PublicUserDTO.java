package com.rij.amethyst_dev.DTO.User.Public;

import com.rij.amethyst_dev.DTO.User.IUserDTO;
import com.rij.amethyst_dev.DTO.User.Private.BaseDiscordDTO;

public record PublicUserDTO(
        Long id,
        BaseDiscordDTO discordUser,
        String minecraftName,
        boolean banned
) implements IUserDTO {
}

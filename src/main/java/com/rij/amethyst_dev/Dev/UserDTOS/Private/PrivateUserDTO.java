package com.rij.amethyst_dev.Dev.UserDTOS.Private;

public record PrivateUserDTO(
        Long id,
        BaseDiscordDTO discordUser,
        String minecraftName,
        boolean hasPayed,
        boolean banned,
        boolean admin
) {

}

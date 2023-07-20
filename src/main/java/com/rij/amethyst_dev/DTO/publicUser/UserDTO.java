package com.rij.amethyst_dev.DTO.publicUser;

public record UserDTO(
        Long id,
        DiscordDTO discordUser,
        String minecraftName,
        boolean hasPayed,
        boolean banned,
        boolean admin
) {

}

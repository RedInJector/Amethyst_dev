package com.rij.amethyst_dev.DTO;

import com.rij.amethyst_dev.DTO.publicUser.DiscordDTO;
import com.rij.amethyst_dev.DTO.publicUser.UserDTO;
import com.rij.amethyst_dev.models.Userdb.Discord.DiscordUser;
import com.rij.amethyst_dev.models.Userdb.User;
import net.dv8tion.jda.api.entities.Role;

import java.util.function.Function;

public class DTOMapper {

    public static Function<DiscordUser, DiscordDTO> DTOFromDiscordUser =
            discordUser -> new DiscordDTO(
                    discordUser.getDiscordId(),
                    discordUser.getPublicUsername()
            );

    public static Function<User, UserDTO> PublicDTOFromUser =
            user -> new UserDTO(
                    user.getId(),
                    DTOFromDiscordUser.apply(user.getDiscordUser()),
                    user.getMinecraftPlayer() == null ? null : user.getMinecraftPlayer().getPlayerName(),
                    user.isHasPayed(),
                    user.isBanned(),
                    user.isAdmin()
            );

    public static Function<Role, DiscordRoleDTO> DTOFromRole =
            role -> {
                if(role.getColor() == null)
                    return new DiscordRoleDTO(
                            role.getName(),
                            197,
                            202,
                            206

                    );
                return new DiscordRoleDTO(
                        role.getName(),
                        role.getColor().getRed(),
                        role.getColor().getGreen(),
                        role.getColor().getBlue()
                );
            };
}

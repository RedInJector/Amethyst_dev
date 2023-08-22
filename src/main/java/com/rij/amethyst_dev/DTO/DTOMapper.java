package com.rij.amethyst_dev.DTO;


import com.rij.amethyst_dev.DTO.User.DiscordRoleDTO;
import net.dv8tion.jda.api.entities.Role;

import java.util.function.Function;

public class DTOMapper {
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

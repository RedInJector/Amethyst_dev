package com.rij.amethyst_dev.models.Userdb;

import com.fasterxml.jackson.annotation.JsonView;
import com.rij.amethyst_dev.Dev.DTO.User.Private.BaseDiscordDTO;
import com.rij.amethyst_dev.models.Views;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "discord_user")
public class DiscordUser {
    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonView(Views.Public.class)
    @Column
    private String discordId;
    @JsonView(Views.Public.class)
    @Column
    private String publicUsername;
    @JsonView(Views.Public.class)
    @Column
    private String tag;
    @JsonView(Views.Public.class)
    @Column
    private String avatarUrl;
    @JsonView(Views.Public.class)
    @Column
    private String discriminator;
    @JsonView(Views.Private.class)
    @Column
    private String email;
    @JsonView(Views.Private.class)
    @Column
    private boolean discordVerified;
//   @JsonView(Views.Public.class)
//   @OneToOne
//   private User user;

    public DiscordUser() {}



    public BaseDiscordDTO getDTO(){
        return new BaseDiscordDTO(
                this.discordId,
                this.publicUsername
        );
    }
}

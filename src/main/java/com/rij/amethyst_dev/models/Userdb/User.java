package com.rij.amethyst_dev.models.Userdb;

import com.fasterxml.jackson.annotation.JsonView;
import com.rij.amethyst_dev.Dev.DTO.User.IUserDTO;
import com.rij.amethyst_dev.Dev.DTO.User.Private.PrivateUserDTO;
import com.rij.amethyst_dev.Dev.DTO.User.Public.PublicUserDTO;
import com.rij.amethyst_dev.models.Userdb.Discord.DiscordUser;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayers.MinecraftPlayer;
import com.rij.amethyst_dev.models.Userdb.Tokens.AccessToken;
import com.rij.amethyst_dev.models.Views;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private DiscordUser discordUser;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private MinecraftPlayer minecraftPlayer;

    private boolean hasPayed = false;

    private boolean banned = false;

    private boolean unbannable = true;

    @JsonView(Views.ServerOnly.class)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Oauth oauth;

    private boolean admin = false;

    @JsonView(Views.ServerOnly.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AccessToken> accessTokens;

    private Integer planUserId;



    public User(){}


    public void addAccessToken(AccessToken accessToken){
        if(this.accessTokens == null)
            this.accessTokens = new ArrayList<>();

        this.accessTokens.add(accessToken);
        accessToken.setUser(this);
    }
    public boolean hasValidAccessToken(String token){
        if(this.accessTokens == null) {
            this.accessTokens = new ArrayList<>();
            return false;
        }

        for (AccessToken item : accessTokens) {
            if(item.getToken().equals(token) && item.getExpiresOn().isAfter(LocalDateTime.now())) {
                return true;
            } else if (item.getToken().equals(token)) {
                accessTokens.remove(item);
            }
        }
        return false;
    }
    public void RemoveAccessToken(String token){
        for (AccessToken accessToken : accessTokens) {
            if (accessToken.getToken().equals(token)){
                accessToken.setUser(null);
            }
        }
    }


    public static User getUserFromDiscordUser(org.redinjector.discord.oAuth2.models.DiscordUser discordUser){
        User user = new User();
        com.rij.amethyst_dev.models.Userdb.Discord.DiscordUser duser = new com.rij.amethyst_dev.models.Userdb.Discord.DiscordUser();
        duser.setDiscordId(discordUser.getId());
        duser.setDiscordVerified(discordUser.isVerified());
        duser.setAvatarUrl(discordUser.getAvatar());
        duser.setEmail(discordUser.getEmail());
        duser.setPublicUsername(discordUser.getUsername());
        duser.setDiscriminator(discordUser.getDiscriminator());

        user.setDiscordUser(duser);
        return user;
    }

    public IUserDTO toPrivateDTO() {
        return createDTO(true);
    }

    public IUserDTO toPublicDTO() {
        return createDTO(false);
    }

    private IUserDTO createDTO(boolean isPrivate) {
        if (isPrivate) {
            return new PrivateUserDTO(
                    id,
                    getDiscordUser().getDTO(),
                    minecraftPlayer == null ? null : minecraftPlayer.getPlayerName(),
                    isHasPayed(),
                    isBanned(),
                    isAdmin(),
                    unbannable
            );
        } else {
            return new PublicUserDTO(
                    id,
                    getDiscordUser().getDTO(),
                    minecraftPlayer == null ? null : minecraftPlayer.getPlayerName(),
                    isBanned()
            );
        }
    }

}

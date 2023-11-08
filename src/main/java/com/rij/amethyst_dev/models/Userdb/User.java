package com.rij.amethyst_dev.models.Userdb;

import com.fasterxml.jackson.annotation.JsonView;
import com.rij.amethyst_dev.DTO.User.IUserDTO;
import com.rij.amethyst_dev.DTO.User.Private.PrivateUserDTO;
import com.rij.amethyst_dev.DTO.User.Public.PublicUserDTO;
import com.rij.amethyst_dev.Enums.UserRoles;
import com.rij.amethyst_dev.models.Views;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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


    @JsonView(Views.ServerOnly.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AccessToken> accessTokens;

    private Integer planUserId;

    @Enumerated(EnumType.STRING)
    private UserRoles role;


    public User(){}

    public boolean isAdmin(){
        return role.hasPermission(UserRoles.EDITOR);
    }


    public void addAccessToken(AccessToken accessToken){
        if(this.accessTokens == null)
            this.accessTokens = new ArrayList<>();

        this.accessTokens.add(accessToken);
        accessToken.setUser(this);
    }

    public boolean isAccessTokenValid(String token) {
        if (this.accessTokens == null) {
            this.accessTokens = new ArrayList<>();
            return false;
        }

        Iterator<AccessToken> iterator = accessTokens.iterator();


        while (iterator.hasNext()) {
            AccessToken item = iterator.next();
            if (item.getToken().equals(token) && item.getExpiresOn().isAfter(LocalDateTime.now())) {
                return true;
            } else if (item.getToken().equals(token)) {
                iterator.remove();
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



    public static User getFromMokuluDiscordAPIUser(io.mokulu.discord.oauth.model.User discordUser){
        User user = new User();
        DiscordUser duser = new DiscordUser();
        duser.setDiscordId(discordUser.getId());
        duser.setDiscordVerified(discordUser.getVerified());
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
                    unbannable,
                    role
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

package com.rij.amethyst_dev.models.Userdb;

import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    @OneToOne(cascade = CascadeType.PERSIST)
    private DiscordUser discordUser;

    @JsonView(Views.Public.class)
    @OneToOne(cascade = CascadeType.PERSIST)
    private MinecraftPlayer minecraftPlayer;

    @JsonView(Views.Private.class)
    private boolean hasPayed = false;

    @JsonView(Views.Public.class)
    private boolean banned = false;

    @JsonView(Views.ServerOnly.class)
    @OneToOne(cascade = CascadeType.ALL)
    private Oauth oauth;

    @JsonView(Views.Private.class)
    private boolean admin = false;

    @JsonView(Views.ServerOnly.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccessToken> accessTokens;

    @JsonView(Views.ServerOnly.class)
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
        DiscordUser duser = new com.rij.amethyst_dev.models.Userdb.Discord.DiscordUser();
        duser.setDiscordId(discordUser.getId());
        duser.setDiscordVerified(discordUser.isVerified());
        duser.setAvatarUrl(discordUser.getAvatar());
        duser.setEmail(discordUser.getEmail());
        duser.setPublicUsername(discordUser.getUsername());
        duser.setDiscriminator(discordUser.getDiscriminator());

        user.setDiscordUser(duser);
        return new User();
    }

}

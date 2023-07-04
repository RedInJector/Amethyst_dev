package com.rij.amethyst_dev.models.Userdb;

import com.fasterxml.jackson.annotation.JsonView;
import com.rij.amethyst_dev.models.Views;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
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

    @Column
    @JsonView(Views.Private.class)
    private boolean hasPayed = false;

    @Column
    @JsonView(Views.Public.class)
    private boolean Banned = false;

    @JsonView(Views.ServerOnly.class)
    @OneToOne(cascade = CascadeType.ALL)
    private Oauth oauth;

    @JsonView(Views.Private.class)
    private boolean Admin = false;

    @JsonView(Views.ServerOnly.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccessToken> accessTokens;




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

}

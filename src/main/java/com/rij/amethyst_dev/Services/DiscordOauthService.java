package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.Configuration.oAuthConfig;
import io.mokulu.discord.oauth.DiscordOAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordOauthService {

    private final oAuthConfig oauthConfig;
    private DiscordOAuth discordOAuth = null;

    @Autowired
    public DiscordOauthService(com.rij.amethyst_dev.Configuration.oAuthConfig oAuthConfig) {
        this.oauthConfig = oAuthConfig;
    }

    public DiscordOAuth getDiscordOAuth(){
        if(discordOAuth != null) return this.discordOAuth;

        this.discordOAuth = new DiscordOAuth(oauthConfig.id,
                oauthConfig.secret,
                oauthConfig.redirecturi,
                new String[]{"identify", "email"});

        return this.discordOAuth;
    }


}

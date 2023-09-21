package com.rij.amethyst_dev;

import com.rij.amethyst_dev.Configuration.oAuthConfig;
import org.redinjector.discord.oAuth2.DiscordOAuth2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
public class AmethystDevApplication {

    private static oAuthConfig oauthConfig;

    @Autowired
    public void setOAuthConfig(oAuthConfig oAuthConfig) {
        oauthConfig = oAuthConfig;
    }

    public static void main(String[] args) {


        SpringApplication.run(AmethystDevApplication.class, args);

        DiscordOAuth2.setConstraints(oauthConfig.id, oauthConfig.secret, oauthConfig.redirecturi);
    }

}

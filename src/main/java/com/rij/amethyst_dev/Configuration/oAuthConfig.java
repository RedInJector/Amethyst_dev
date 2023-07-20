package com.rij.amethyst_dev.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class oAuthConfig {
    @Value("${discordoauth.id}")
    public String id;
    @Value("${discordoauth.secret}")
    public String secret;
    @Value("${discordoauth.redirecturi}")
    public String redirecturi;
    @Value("${discordoauth.redirecturl}")
    public String redirecturl;
}

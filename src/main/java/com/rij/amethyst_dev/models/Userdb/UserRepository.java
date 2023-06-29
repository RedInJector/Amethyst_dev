package com.rij.amethyst_dev.models.Userdb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByDiscordUserDiscordId(String discordId);
    User findByMinecraftPlayerPlayerName(String name);
    User findByOauthAccessToken(String Token);
    User findByAccessTokensToken(String Token);
    //User findByToken(String Token);
}

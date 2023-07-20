package com.rij.amethyst_dev.models.Userdb.MinecraftPlayers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinecraftPlayerRepository extends JpaRepository<MinecraftPlayer, Integer> {
    MinecraftPlayer findByPlayerName(String discordId);
}

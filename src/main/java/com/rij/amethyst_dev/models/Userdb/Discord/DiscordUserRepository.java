package com.rij.amethyst_dev.models.Userdb.Discord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordUserRepository extends JpaRepository<DiscordUser, Integer> {

}

package com.rij.amethyst_dev.Repositories;
import com.rij.amethyst_dev.models.Userdb.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = { "minecraftPlayer", "discordUser" })
    @Query("SELECT u FROM User u")
    Page<User> getAll(Pageable pageable);
    @EntityGraph(attributePaths = { "minecraftPlayer", "discordUser" })
    @Query("SELECT u FROM User u WHERE u.minecraftPlayer.playerName LIKE %?1%")
    List<User> findByMinecraftName(String name);

    @EntityGraph(attributePaths = { "minecraftPlayer" })
    User findByDiscordUserDiscordId(String discordId);
    @EntityGraph(attributePaths = { "discordUser" })
    User findByMinecraftPlayerPlayerName(String name);
    @EntityGraph(attributePaths = { "discordUser", "minecraftPlayer" })
    User findByAccessTokensToken(String Token);

    @EntityGraph(attributePaths = { "minecraftPlayer", "discordUser" })
    @Query("SELECT u FROM User u INNER JOIN u.minecraftPlayer mp WHERE u.hasPayed = true ORDER BY mp.id")
    Page<User> findUsersWithNonNullMinecraftNameAndHasPayed(Pageable pageable);

    @EntityGraph(attributePaths = { "minecraftPlayer", "discordUser" })
    @Query("SELECT u FROM User u WHERE u.minecraftPlayer.playerName LIKE %?1% AND u.hasPayed = true")
    List<User> findUsersWithMinecraftNameAndHasPayed(String name, Pageable pageable);

    @EntityGraph(attributePaths = { "minecraftPlayer", "discordUser" })
    @Query("SELECT u FROM User u")
    List<User> PagablefindAll(Pageable pageable);
}



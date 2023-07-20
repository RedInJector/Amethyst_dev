package com.rij.amethyst_dev.models.Userdb.MinecraftPlayers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rij.amethyst_dev.models.Views;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "minecraft_player")
public class MinecraftPlayer {
    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JsonView(Views.Public.class)
    private String playerName;
}

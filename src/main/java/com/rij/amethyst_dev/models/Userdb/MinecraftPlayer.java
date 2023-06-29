package com.rij.amethyst_dev.models.Userdb;

import com.fasterxml.jackson.annotation.JsonView;
import com.rij.amethyst_dev.models.Views;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MinecraftPlayer {
    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @JsonView(Views.Public.class)
//    @OneToOne
//    private User user;

    @Column
    @JsonView(Views.Public.class)
    private String playerName;
    @Column
    @JsonView(Views.Public.class)
    private boolean AllowedOnServer;

    @Transient
    @JsonView(Views.Public.class)
    private transient String skinUrl;
}

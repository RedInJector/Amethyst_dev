package com.rij.amethyst_dev.models.oAuth;

import com.rij.amethyst_dev.models.Userdb.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "oauth", schema = "project_amethyst_dev2")
public class Oauth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
    @Column
    private String accessToken;
    @Column
    private String refreshToken;
    @Column
    private LocalDateTime expiresOn;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public Oauth(){}
}

package com.rij.amethyst_dev.Repositories;

import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface oAuth2Repository extends JpaRepository<Oauth, Integer> {
    Oauth findByUser(User user);
    Oauth findByAccessToken(String token);
    List<Oauth> findAllByUser_Id(Long userId);
}

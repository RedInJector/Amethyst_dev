package com.rij.amethyst_dev.models.Userdb.Tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokensRepository extends JpaRepository<AccessToken, Integer> {

}
package com.rij.amethyst_dev.Helpers;

import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class Authorizator {
    private UserService userService;

    public Authorizator(UserService userService) {
        this.userService = userService;
    }

    public User authorizedUser(String accessToken){
        if(accessToken.isEmpty())
            return null;

        User user = userService.getUserByAccessToken(accessToken);
        if(user == null)
            return null;

        if(!user.isAccessTokenValid(accessToken))
            return null;

        return user;
    }
}

package com.rij.amethyst_dev.Helpers;

import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import org.springframework.stereotype.Service;

@Service
public class Authorizator {
    private final UserService userService;

    public Authorizator(UserService userService) {
        this.userService = userService;
    }

    public boolean isAuthorized(String accessToken){
        User user = userService.getUserByAccessToken(accessToken);

        if(user == null)
            return false;

        if(!user.hasValidAccessToken(accessToken))
            return false;

        return true;
    }
}
